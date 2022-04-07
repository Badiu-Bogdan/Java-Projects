package repository.XML;

import domain.BaseEntity;
import domain.Pet.Pet;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.InMemoryRepositoryException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryPetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XMLRepositoryPet<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;
    private String fileName; // ex: text.xml

    public XMLRepositoryPet(Validator<T> validator, String fileName) {
        this.validator = validator;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Save a pet to the repository
     *
     * @param pet : Pet to be saved to the repository
     * @throws XMLRepositoryPetException
     *          if some error regarding the document builder occurs
     */
    public T saveToXML(Pet pet) throws XMLRepositoryPetException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            addPetToDom(pet, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

            return (T) pet;

        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
            throw new XMLRepositoryPetException(e.getMessage());
        }
    }

    /**
     * Add a Pet to the document object model
     *
     * @param pet : Pet to be added
     *        document : Document to be used
     */
    public static void addPetToDom(Pet pet, Document document) {
        Element root = document.getDocumentElement();
        Node petNode = createNodeFromPet(pet, document);

        root.appendChild(petNode);
    }

    /**
     * Create a Node element given a Pet object
     *
     * @param pet : Pet to be saved to the repository
     *        document : Document to be used
     * @return petElement : Node containing the pet element
     */
    public static Node createNodeFromPet(Pet pet, Document document) {
        Element petElement = document.createElement("pet");

        addChildWithTextContent(document, petElement, "id", pet.getId().toString());
        addChildWithTextContent(document, petElement, "serialNumber", pet.getSerialNumber());
        addChildWithTextContent(document, petElement, "name", pet.getName());
        addChildWithTextContent(document, petElement, "breed", pet.getBreed());
        addChildWithTextContent(document, petElement, "birthYear", Integer.toString(pet.getBirthDate()));

        return petElement;
    }

    /**
     * Create a Pet object given a Node element
     *
     * @param petElement : Pet to be saved to the repository
     * @return pet : Pet the generated Pet
     */
    public static Pet createPetFromNode(Element petElement) {

        Pet pet = new Pet();

        pet.setId(Long.parseLong(petElement.getElementsByTagName("id").item(0).getTextContent()));
        pet.setSerialNumber(petElement.getElementsByTagName("serialNumber").item(0).getTextContent());
        pet.setName(petElement.getElementsByTagName("name").item(0).getTextContent());
        pet.setBreed(petElement.getElementsByTagName("breed").item(0).getTextContent());
        pet.setBirthDate(Integer.parseInt(petElement.getElementsByTagName("birthYear").item(0).getTextContent()));

        return pet;
    }

    /**
     * Add a Child node with text content to the dom
     *
     * @param document : Document to be used for creating an element
     *        parent : Element parent in which the content is added
     *        tagName : String name of the tag
     *        textContent : String to be added
     * @throws XMLRepositoryPetException
     *          if some error regarding the document builder occurs
     */
    public static void addChildWithTextContent(Document document, Element parent, String tagName, String textContent) {
        Element childElement = document.createElement(tagName);
        childElement.setTextContent(textContent);
        parent.appendChild(childElement);
    }

    /**
     * Load pets from the xml file
     *
     * @throws XMLRepositoryPetException
     *          if some error regarding the document builder occurs
     */
    public List<Pet> loadFromXML() throws XMLRepositoryPetException{
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document root = db.parse("data/xml/" + fileName + ".xml");
            Element petsRoot = root.getDocumentElement();
            NodeList petList = petsRoot.getChildNodes();
            List<Pet> pets = new ArrayList<>();
            for (int i = 0; i < petList.getLength(); ++i) {
                if (!(petList.item(i) instanceof Element))
                    continue;
                Pet pet = createPetFromNode((Element) petList.item(i));
                pets.add(pet);
            }
            return pets;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XMLRepositoryPetException("XMLRepositoryPetException: " + e.getMessage());
        }
    }

    /**
     * Save a list of pets to the repository
     *
     * @param entities : List<Pet> to be saved to the repository
     * @throws XMLRepositoryPetException
     *          if some error from saveToXML method arises
     */
    public void saveEntitiesToXML(List<Pet> entities) throws XMLRepositoryPetException {
        entities.forEach(this::saveToXML);
    }

    /**
     * Save a map of pets to the repository
     *
     * @param entities : Map<ID, Pet> to be saved to the repository
     * @throws XMLRepositoryPetException
     *          if some error from saveToXML method arises
     */
    public void saveMapToXML(Map<ID, Pet> entities) throws XMLRepositoryPetException {
        entities.forEach((key, value) -> saveToXML(value));
    }

    /**
     * Converts a list of entities to a map of the form <ID,Entity>
     *
     * @param entities : List<Pet> to be converted
     */
    public Map<ID, Pet> convertListToMap(List<Pet> entities){
        Map<ID, Pet> map = new HashMap<>();
        entities.forEach(pet -> map.put((ID) pet.getId(), pet));
        return map;
    }

    /**
     * Deletes all entries in the repository
     *
     * @throws XMLRepositoryPetException
     *         if some error from saveToXML method arises
     */
    public void deleteAll() throws XMLRepositoryPetException{
        List<Pet> pets = this.loadFromXML();
        pets.forEach(pet -> this.deletePet((ID) pet.getId()));
    }

    /**
     * Deletes an entry from the repository
     *
     * @param id : ID of the Pet to be deleted
     * @throws XMLRepositoryPetException
     *         if some error occurs
     */
    public void deletePet(ID id) throws XMLRepositoryPetException {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//pets/pet[id/text()=" + id + "]");

            Node petNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            petNode.getParentNode().removeChild(petNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
            throw new XMLRepositoryPetException("failed to remove pet.");
        }
    }

    @Override
    public Optional<T> findOne(ID id) {

        List<Pet> entities = this.loadFromXML();
        Map<ID, Pet> map = this.convertListToMap(entities);
        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        List<Pet> entities = this.loadFromXML();
        Set<Pet> allEntities = entities.stream().map(entry -> entry).collect(Collectors.toSet());
        return (Iterable<T>) allEntities;
    }

    @Override
    public Optional<T> save(T entity) throws IllegalArgumentException, ValidatorException {

        Optional<T> result = Optional.empty();

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity); // throws directly

        // check id taken
        List<Pet> entities = this.loadFromXML();
        result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElse(saveToXML((Pet)entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        List<Pet> entities = this.loadFromXML();
        Map<ID, Pet> map = this.convertListToMap(entities);

        Pet result = map.remove(id);

        try {
            this.deletePet(id);
        }catch(Exception e) {}

        return (Optional<T>) Optional.ofNullable(result);
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        List<Pet> entities = this.loadFromXML();
        Map<ID, Pet> map = this.convertListToMap(entities);


        Optional<T> result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElseGet(() -> {
            this.deletePet(entity.getId());
            this.saveToXML((Pet) entity);
            return null;
        });


        // fail to update? return entity
        return result;
    }
}

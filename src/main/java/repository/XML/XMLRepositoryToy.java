package repository.XML;

import domain.BaseEntity;
import domain.Toy.Toy;
import domain.Toy.Toy;
import domain.Toy.Toy;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryToyException;
import repository.XML.exceptions.XMLRepositoryToyException;
import repository.XML.exceptions.XMLRepositoryToyException;

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

public class XMLRepositoryToy<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;
    private String fileName; // ex: text.xml

    public XMLRepositoryToy(Validator<T> validator, String fileName) {
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
     * Save a toy to the repository
     *
     * @param toy : Toy to be saved to the repository
     * @throws XMLRepositoryToyException
     *          if some error regarding the document builder occurs
     */
    public T saveToXML(Toy toy) throws XMLRepositoryToyException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            addToyToDom(toy, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

            return (T) toy;

        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
            throw new XMLRepositoryToyException(e.getMessage());
        }
    }

    /**
     * Add a Toy to the document object model
     *
     * @param toy : Toy to be added
     *        document : Document to be used
     */
    public static void addToyToDom(Toy toy, Document document) {
        Element root = document.getDocumentElement();
        Node toyNode = createNodeFromToy(toy, document);

        root.appendChild(toyNode);
    }

    /**
     * Create a Node element given a Toy object
     *
     * @param toy : Toy to be saved to the repository
     *        document : Document to be used
     * @return toyElement : Node containing the toy element
     */
    public static Node createNodeFromToy(Toy toy, Document document) {
        Element toyElement = document.createElement("toy");

        addChildWithTextContent(document, toyElement, "id", toy.getId().toString());
        addChildWithTextContent(document, toyElement, "serialNumber", toy.getSerialNumber());
        addChildWithTextContent(document, toyElement, "name", toy.getName());
        addChildWithTextContent(document, toyElement, "weight", Integer.toString(toy.getWeight()));
        addChildWithTextContent(document, toyElement, "material", toy.getMaterial());
        addChildWithTextContent(document, toyElement, "price", Double.toString(toy.getPrice()));

        return toyElement;
    }

    /**
     * Create a Toy object given a Node element
     *
     * @param toyElement : Toy to be saved to the repository
     * @return toy : Toy the generated Toy
     */
    public static Toy createToyFromNode(Element toyElement) {

        Toy toy = new Toy();

        toy.setId(Long.parseLong(toyElement.getElementsByTagName("id").item(0).getTextContent()));
        toy.setSerialNumber(toyElement.getElementsByTagName("serialNumber").item(0).getTextContent());
        toy.setName(toyElement.getElementsByTagName("name").item(0).getTextContent());
        toy.setWeight(Integer.parseInt(toyElement.getElementsByTagName("weight").item(0).getTextContent()));
        toy.setMaterial(toyElement.getElementsByTagName("material").item(0).getTextContent());
        toy.setPrice(Double.parseDouble(toyElement.getElementsByTagName("price").item(0).getTextContent()));

        return toy;
    }

    /**
     * Add a Child node with text content to the dom
     *
     * @param document : Document to be used for creating an element
     *        parent : Element parent in which the content is added
     *        tagName : String name of the tag
     *        textContent : String to be added
     * @throws XMLRepositoryToyException
     *          if some error regarding the document builder occurs
     */
    public static void addChildWithTextContent(Document document, Element parent, String tagName, String textContent) {
        Element childElement = document.createElement(tagName);
        childElement.setTextContent(textContent);
        parent.appendChild(childElement);
    }

    /**
     * Load toys from the xml file
     *
     * @throws XMLRepositoryToyException
     *          if some error regarding the document builder occurs
     */
    public List<Toy> loadFromXML() throws XMLRepositoryToyException{
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document root = db.parse("data/xml/" + fileName + ".xml");
            Element toysRoot = root.getDocumentElement();
            NodeList toyList = toysRoot.getChildNodes();
            List<Toy> toys = new ArrayList<>();
            for (int i = 0; i < toyList.getLength(); ++i) {
                if (!(toyList.item(i) instanceof Element))
                    continue;
                Toy toy = createToyFromNode((Element) toyList.item(i));
                toys.add(toy);
            }
            return toys;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XMLRepositoryToyException("XMLRepositoryToyException: " + e.getMessage());
        }
    }

    /**
     * Save a list of toys to the repository
     *
     * @param entities : List<Toy> to be saved to the repository
     * @throws XMLRepositoryToyException
     *          if some error from saveToXML method arises
     */
    public void saveEntitiesToXML(List<Toy> entities) throws XMLRepositoryToyException {
        entities.forEach(this::saveToXML);
    }

    /**
     * Save a map of toys to the repository
     *
     * @param entities : Map<ID, Toy> to be saved to the repository
     * @throws XMLRepositoryToyException
     *          if some error from saveToXML method arises
     */
    public void saveMapToXML(Map<ID, Toy> entities) throws XMLRepositoryToyException {
        entities.forEach((key, value) -> saveToXML(value));
    }

    /**
     * Converts a list of entities to a map of the form <ID,Entity>
     *
     * @param entities : List<Toy> to be converted
     */
    public Map<ID, Toy> convertListToMap(List<Toy> entities){
        Map<ID, Toy> map = new HashMap<>();
        entities.forEach(toy -> map.put((ID) toy.getId(), toy));
        return map;
    }

    /**
     * Deletes all entries in the repository
     *
     * @throws XMLRepositoryToyException
     *         if some error from saveToXML method arises
     */
    public void deleteAll() throws XMLRepositoryToyException{
        List<Toy> toys = this.loadFromXML();
        toys.forEach(toy -> this.deleteToy((ID) toy.getId()));
    }

    /**
     * Deletes an entry from the repository
     *
     * @param id : ID of the Toy to be deleted
     * @throws XMLRepositoryToyException
     *         if some error occurs
     */
    public void deleteToy(ID id) throws XMLRepositoryToyException {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//toys/toy[id/text()=" + id + "]");

            Node toyNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            toyNode.getParentNode().removeChild(toyNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
            throw new XMLRepositoryToyException("failed to remove toy.");
        }
    }

    @Override
    public Optional<T> findOne(ID id) {

        List<Toy> entities = this.loadFromXML();
        Map<ID, Toy> map = this.convertListToMap(entities);
        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        List<Toy> entities = this.loadFromXML();
        Set<Toy> allEntities = entities.stream().map(entry -> entry).collect(Collectors.toSet());
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
        List<Toy> entities = this.loadFromXML();
        result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElse(saveToXML((Toy)entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        List<Toy> entities = this.loadFromXML();
        Map<ID, Toy> map = this.convertListToMap(entities);

        Toy result = map.remove(id);

        try {
            this.deleteToy(id);
        }catch(Exception e) {}

        return (Optional<T>) Optional.ofNullable(result);
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        List<Toy> entities = this.loadFromXML();
        Map<ID, Toy> map = this.convertListToMap(entities);


        Optional<T> result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElseGet(() -> {
            this.deleteToy(entity.getId());
            this.saveToXML((Toy) entity);
            return null;
        });


        // fail to update? return entity
        return result;
    }
}

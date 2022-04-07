package repository.XML;

import domain.BaseEntity;
import domain.Adoption.Adoption;
import domain.Adoption.Adoption;
import domain.Adoption.Adoption;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryAdoptionException;
import repository.XML.exceptions.XMLRepositoryAdoptionException;
import repository.XML.exceptions.XMLRepositoryAdoptionException;

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

public class XMLRepositoryAdoption<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;
    private String fileName; // ex: text.xml

    public XMLRepositoryAdoption(Validator<T> validator, String fileName) {
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
     * Save a adoption to the repository
     *
     * @param adoption : Adoption to be saved to the repository
     * @throws XMLRepositoryAdoptionException
     *          if some error regarding the document builder occurs
     */
    public T saveToXML(Adoption adoption) throws XMLRepositoryAdoptionException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            addAdoptionToDom(adoption, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

            return (T) adoption;

        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
            throw new XMLRepositoryAdoptionException(e.getMessage());
        }
    }

    /**
     * Add a Adoption to the document object model
     *
     * @param adoption : Adoption to be added
     *        document : Document to be used
     */
    public static void addAdoptionToDom(Adoption adoption, Document document) {
        Element root = document.getDocumentElement();
        Node adoptionNode = createNodeFromAdoption(adoption, document);

        root.appendChild(adoptionNode);
    }

    /**
     * Create a Node element given a Adoption object
     *
     * @param adoption : Adoption to be saved to the repository
     *        document : Document to be used
     * @return adoptionElement : Node containing the adoption element
     */
    public static Node createNodeFromAdoption(Adoption adoption, Document document) {
        Element adoptionElement = document.createElement("adoption");

        addChildWithTextContent(document, adoptionElement, "id", adoption.getId().toString());
        addChildWithTextContent(document, adoptionElement, "serialNumber", adoption.getSerialNumber());
        addChildWithTextContent(document, adoptionElement, "clientId", Long.toString(adoption.getClientId()));
        addChildWithTextContent(document, adoptionElement, "petId", Long.toString(adoption.getPetId()));
        addChildWithTextContent(document, adoptionElement, "adoptionYear", Integer.toString(adoption.getAdoptionYear()));

        return adoptionElement;
    }

    /**
     * Create a Adoption object given a Node element
     *
     * @param adoptionElement : Adoption to be saved to the repository
     * @return adoption : Adoption the generated Adoption
     */
    public static Adoption createAdoptionFromNode(Element adoptionElement) {

        Adoption adoption = new Adoption();

        adoption.setId(Long.parseLong(adoptionElement.getElementsByTagName("id").item(0).getTextContent()));
        adoption.setSerialNumber(adoptionElement.getElementsByTagName("serialNumber").item(0).getTextContent());
        adoption.setClientId(Long.parseLong(adoptionElement.getElementsByTagName("clientId").item(0).getTextContent()));
        adoption.setPetId(Long.parseLong(adoptionElement.getElementsByTagName("petId").item(0).getTextContent()));
        adoption.setAdoptionYear(Integer.parseInt(adoptionElement.getElementsByTagName("adoptionYear").item(0).getTextContent()));

        return adoption;
    }

    /**
     * Add a Child node with text content to the dom
     *
     * @param document : Document to be used for creating an element
     *        parent : Element parent in which the content is added
     *        tagName : String name of the tag
     *        textContent : String to be added
     * @throws XMLRepositoryAdoptionException
     *          if some error regarding the document builder occurs
     */
    public static void addChildWithTextContent(Document document, Element parent, String tagName, String textContent) {
        Element childElement = document.createElement(tagName);
        childElement.setTextContent(textContent);
        parent.appendChild(childElement);
    }

    /**
     * Load adoptions from the xml file
     *
     * @throws XMLRepositoryAdoptionException
     *          if some error regarding the document builder occurs
     */
    public List<Adoption> loadFromXML() throws XMLRepositoryAdoptionException{
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document root = db.parse("data/xml/" + fileName + ".xml");
            Element adoptionsRoot = root.getDocumentElement();
            NodeList adoptionList = adoptionsRoot.getChildNodes();
            List<Adoption> adoptions = new ArrayList<>();
            for (int i = 0; i < adoptionList.getLength(); ++i) {
                if (!(adoptionList.item(i) instanceof Element))
                    continue;
                Adoption adoption = createAdoptionFromNode((Element) adoptionList.item(i));
                adoptions.add(adoption);
            }
            return adoptions;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XMLRepositoryAdoptionException("XMLRepositoryAdoptionException: " + e.getMessage());
        }
    }

    /**
     * Save a list of adoptions to the repository
     *
     * @param entities : List<Adoption> to be saved to the repository
     * @throws XMLRepositoryAdoptionException
     *          if some error from saveToXML method arises
     */
    public void saveEntitiesToXML(List<Adoption> entities) throws XMLRepositoryAdoptionException {
        entities.forEach(this::saveToXML);
    }

    /**
     * Save a map of adoptions to the repository
     *
     * @param entities : Map<ID, Adoption> to be saved to the repository
     * @throws XMLRepositoryAdoptionException
     *          if some error from saveToXML method arises
     */
    public void saveMapToXML(Map<ID, Adoption> entities) throws XMLRepositoryAdoptionException {
        entities.forEach((key, value) -> saveToXML(value));
    }

    /**
     * Converts a list of entities to a map of the form <ID,Entity>
     *
     * @param entities : List<Adoption> to be converted
     */
    public Map<ID, Adoption> convertListToMap(List<Adoption> entities){
        Map<ID, Adoption> map = new HashMap<>();
        entities.forEach(adoption -> map.put((ID) adoption.getId(), adoption));
        return map;
    }

    /**
     * Deletes all entries in the repository
     *
     * @throws XMLRepositoryAdoptionException
     *         if some error from saveToXML method arises
     */
    public void deleteAll() throws XMLRepositoryAdoptionException{
        List<Adoption> adoptions = this.loadFromXML();
        adoptions.forEach(adoption -> this.deleteAdoption((ID) adoption.getId()));
    }

    /**
     * Deletes an entry from the repository
     *
     * @param id : ID of the Adoption to be deleted
     * @throws XMLRepositoryAdoptionException
     *         if some error occurs
     */
    public void deleteAdoption(ID id) throws XMLRepositoryAdoptionException {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//adoptions/adoption[id/text()=" + id + "]");

            Node adoptionNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            adoptionNode.getParentNode().removeChild(adoptionNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
            throw new XMLRepositoryAdoptionException("failed to remove adoption.");
        }
    }

    @Override
    public Optional<T> findOne(ID id) {

        List<Adoption> entities = this.loadFromXML();
        Map<ID, Adoption> map = this.convertListToMap(entities);
        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        List<Adoption> entities = this.loadFromXML();
        Set<Adoption> allEntities = entities.stream().map(entry -> entry).collect(Collectors.toSet());
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
        List<Adoption> entities = this.loadFromXML();
        result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElse(saveToXML((Adoption)entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        List<Adoption> entities = this.loadFromXML();
        Map<ID, Adoption> map = this.convertListToMap(entities);

        Adoption result = map.remove(id);

        try {
            this.deleteAdoption(id);
        }catch(Exception e) {}

        return (Optional<T>) Optional.ofNullable(result);
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        List<Adoption> entities = this.loadFromXML();
        Map<ID, Adoption> map = this.convertListToMap(entities);


        Optional<T> result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElseGet(() -> {
            this.deleteAdoption(entity.getId());
            this.saveToXML((Adoption) entity);
            return null;
        });


        // fail to update? return entity
        return result;
    }
}

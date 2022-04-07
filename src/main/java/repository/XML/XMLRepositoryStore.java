package repository.XML;

import domain.BaseEntity;
import domain.Purchase.Purchase;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryStoreException;

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

public class XMLRepositoryStore<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;
    private String fileName; // ex: text.xml

    public XMLRepositoryStore(Validator<T> validator, String fileName) {
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
     * Save a purchase to the repository
     *
     * @param purchase : Purchase to be saved to the repository
     * @throws XMLRepositoryStoreException
     *          if some error regarding the document builder occurs
     */
    public T saveToXML(Purchase purchase) throws XMLRepositoryStoreException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            addPurchaseToDom(purchase, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

            return (T) purchase;

        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
            throw new XMLRepositoryStoreException(e.getMessage());
        }
    }

    /**
     * Add a Purchase to the document object model
     *
     * @param purchase : Purchase to be added
     *        document : Document to be used
     */
    public static void addPurchaseToDom(Purchase purchase, Document document) {
        Element root = document.getDocumentElement();
        Node purchaseNode = createNodeFromPurchase(purchase, document);

        root.appendChild(purchaseNode);
    }

    /**
     * Create a Node element given a Purchase object
     *
     * @param purchase : Purchase to be saved to the repository
     *        document : Document to be used
     * @return purchaseElement : Node containing the purchase element
     */
    public static Node createNodeFromPurchase(Purchase purchase, Document document) {
        Element purchaseElement = document.createElement("purchase");

        addChildWithTextContent(document, purchaseElement, "id", purchase.getId().toString());
        addChildWithTextContent(document, purchaseElement, "serialNumber", purchase.getSerialNumber());
        addChildWithTextContent(document, purchaseElement, "clientId", Long.toString(purchase.getClientId()));
        addChildWithTextContent(document, purchaseElement, "toyId", Long.toString(purchase.getToyId()));
        addChildWithTextContent(document, purchaseElement, "purchaseYear", Integer.toString(purchase.getPurchaseYear()));

        return purchaseElement;
    }

    /**
     * Create a Purchase object given a Node element
     *
     * @param purchaseElement : Purchase to be saved to the repository
     * @return purchase : Purchase the generated Purchase
     */
    public static Purchase createPurchaseFromNode(Element purchaseElement) {

        Purchase purchase = new Purchase();

        purchase.setId(Long.parseLong(purchaseElement.getElementsByTagName("id").item(0).getTextContent()));
        purchase.setSerialNumber(purchaseElement.getElementsByTagName("serialNumber").item(0).getTextContent());
        purchase.setClientId(Long.parseLong(purchaseElement.getElementsByTagName("clientId").item(0).getTextContent()));
        purchase.setToyId(Long.parseLong(purchaseElement.getElementsByTagName("toyId").item(0).getTextContent()));
        purchase.setPurchaseYear(Integer.parseInt(purchaseElement.getElementsByTagName("purchaseYear").item(0).getTextContent()));

        return purchase;
    }

    /**
     * Add a Child node with text content to the dom
     *
     * @param document : Document to be used for creating an element
     *        parent : Element parent in which the content is added
     *        tagName : String name of the tag
     *        textContent : String to be added
     * @throws XMLRepositoryStoreException
     *          if some error regarding the document builder occurs
     */
    public static void addChildWithTextContent(Document document, Element parent, String tagName, String textContent) {
        Element childElement = document.createElement(tagName);
        childElement.setTextContent(textContent);
        parent.appendChild(childElement);
    }

    /**
     * Load purchases from the xml file
     *
     * @throws XMLRepositoryStoreException
     *          if some error regarding the document builder occurs
     */
    public List<Purchase> loadFromXML() throws XMLRepositoryStoreException{
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document root = db.parse("data/xml/" + fileName + ".xml");
            Element purchasesRoot = root.getDocumentElement();
            NodeList purchaseList = purchasesRoot.getChildNodes();
            List<Purchase> purchases = new ArrayList<>();
            for (int i = 0; i < purchaseList.getLength(); ++i) {
                if (!(purchaseList.item(i) instanceof Element))
                    continue;
                Purchase purchase = createPurchaseFromNode((Element) purchaseList.item(i));
                purchases.add(purchase);
            }
            return purchases;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XMLRepositoryStoreException("XMLRepositoryStoreException: " + e.getMessage());
        }
    }

    /**
     * Save a list of purchases to the repository
     *
     * @param entities : List<Purchase> to be saved to the repository
     * @throws XMLRepositoryStoreException
     *          if some error from saveToXML method arises
     */
    public void saveEntitiesToXML(List<Purchase> entities) throws XMLRepositoryStoreException {
        entities.forEach(this::saveToXML);
    }

    /**
     * Save a map of purchases to the repository
     *
     * @param entities : Map<ID, Purchase> to be saved to the repository
     * @throws XMLRepositoryStoreException
     *          if some error from saveToXML method arises
     */
    public void saveMapToXML(Map<ID, Purchase> entities) throws XMLRepositoryStoreException {
        entities.forEach((key, value) -> saveToXML(value));
    }

    /**
     * Converts a list of entities to a map of the form <ID,Entity>
     *
     * @param entities : List<Purchase> to be converted
     */
    public Map<ID, Purchase> convertListToMap(List<Purchase> entities){
        Map<ID, Purchase> map = new HashMap<>();
        entities.forEach(purchase -> map.put((ID) purchase.getId(), purchase));
        return map;
    }

    /**
     * Deletes all entries in the repository
     *
     * @throws XMLRepositoryStoreException
     *         if some error from saveToXML method arises
     */
    public void deleteAll() throws XMLRepositoryStoreException{
        List<Purchase> purchases = this.loadFromXML();
        purchases.forEach(purchase -> this.deletePurchase((ID) purchase.getId()));
    }

    /**
     * Deletes an entry from the repository
     *
     * @param id : ID of the Purchase to be deleted
     * @throws XMLRepositoryStoreException
     *         if some error occurs
     */
    public void deletePurchase(ID id) throws XMLRepositoryStoreException {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//purchases/purchase[id/text()=" + id + "]");

            Node purchaseNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            purchaseNode.getParentNode().removeChild(purchaseNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
            throw new XMLRepositoryStoreException("failed to remove purchase.");
        }
    }

    @Override
    public Optional<T> findOne(ID id) {

        List<Purchase> entities = this.loadFromXML();
        Map<ID, Purchase> map = this.convertListToMap(entities);
        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        List<Purchase> entities = this.loadFromXML();
        Set<Purchase> allEntities = entities.stream().map(entry -> entry).collect(Collectors.toSet());
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
        List<Purchase> entities = this.loadFromXML();
        result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElse(saveToXML((Purchase)entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        List<Purchase> entities = this.loadFromXML();
        Map<ID, Purchase> map = this.convertListToMap(entities);

        Purchase result = map.remove(id);

        try {
            this.deletePurchase(id);
        }catch(Exception e) {}

        return (Optional<T>) Optional.ofNullable(result);
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        List<Purchase> entities = this.loadFromXML();
        Map<ID, Purchase> map = this.convertListToMap(entities);


        Optional<T> result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElseGet(() -> {
            this.deletePurchase(entity.getId());
            this.saveToXML((Purchase) entity);
            return null;
        });


        // fail to update? return entity
        return result;
    }
}

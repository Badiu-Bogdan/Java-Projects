package repository.XML;

import domain.BaseEntity;
import domain.Client.Client;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryClientException;

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

public class XMLRepositoryClient<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;
    private String fileName; // ex: text.xml

    public XMLRepositoryClient(Validator<T> validator, String fileName) {
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
     * Save a client to the repository
     *
     * @param client : Client to be saved to the repository
     * @throws XMLRepositoryClientException
     *          if some error regarding the document builder occurs
     */
    public T saveToXML(Client client) throws XMLRepositoryClientException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            addClientToDom(client, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

            return (T) client;

        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
            throw new XMLRepositoryClientException(e.getMessage());
        }
    }

    /**
     * Add a Client to the document object model
     *
     * @param client : Client to be added
     *        document : Document to be used
     */
    public static void addClientToDom(Client client, Document document) {
        Element root = document.getDocumentElement();
        Node clientNode = createNodeFromClient(client, document);

        root.appendChild(clientNode);
    }

    /**
     * Create a Node element given a Client object
     *
     * @param client : Client to be saved to the repository
     *        document : Document to be used
     * @return clientElement : Node containing the client element
     */
    public static Node createNodeFromClient(Client client, Document document) {
        Element clientElement = document.createElement("client");

        addChildWithTextContent(document, clientElement, "id", client.getId().toString());
        addChildWithTextContent(document, clientElement, "serialNumber", client.getSerialNumber());
        addChildWithTextContent(document, clientElement, "name", client.getName());
        addChildWithTextContent(document, clientElement, "address", client.getAddress());
        addChildWithTextContent(document, clientElement, "yearOfRegistration", Integer.toString(client.getYearOfRegistration()));

        return clientElement;
    }

    /**
     * Create a Client object given a Node element
     *
     * @param clientElement : Client to be saved to the repository
     * @return client : Client the generated Client
     */
    public static Client createClientFromNode(Element clientElement) {

        Client client = new Client();

        client.setId(Long.parseLong(clientElement.getElementsByTagName("id").item(0).getTextContent()));
        client.setSerialNumber(clientElement.getElementsByTagName("serialNumber").item(0).getTextContent());
        client.setName(clientElement.getElementsByTagName("name").item(0).getTextContent());
        client.setAddress(clientElement.getElementsByTagName("address").item(0).getTextContent());
        client.setYearOfRegistration(Integer.parseInt(clientElement.getElementsByTagName("yearOfRegistration").item(0).getTextContent()));


        return client;
    }

    /**
     * Add a Child node with text content to the dom
     *
     * @param document : Document to be used for creating an element
     *        parent : Element parent in which the content is added
     *        tagName : String name of the tag
     *        textContent : String to be added
     * @throws XMLRepositoryClientException
     *          if some error regarding the document builder occurs
     */
    public static void addChildWithTextContent(Document document, Element parent, String tagName, String textContent) {
        Element childElement = document.createElement(tagName);
        childElement.setTextContent(textContent);
        parent.appendChild(childElement);
    }

    /**
     * Load clients from the xml file
     *
     * @throws XMLRepositoryClientException
     *          if some error regarding the document builder occurs
     */
    public List<Client> loadFromXML() throws XMLRepositoryClientException{
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document root = db.parse("data/xml/" + fileName + ".xml");
            Element clientsRoot = root.getDocumentElement();
            NodeList clientList = clientsRoot.getChildNodes();
            List<Client> clients = new ArrayList<>();
            for (int i = 0; i < clientList.getLength(); ++i) {
                if (!(clientList.item(i) instanceof Element))
                    continue;
                Client client = createClientFromNode((Element) clientList.item(i));
                clients.add(client);
            }
            return clients;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XMLRepositoryClientException("XMLRepositoryClientException: " + e.getMessage());
        }
    }

    /**
     * Save a list of clients to the repository
     *
     * @param entities : List<Client> to be saved to the repository
     * @throws XMLRepositoryClientException
     *          if some error from saveToXML method arises
     */
    public void saveEntitiesToXML(List<Client> entities) throws XMLRepositoryClientException {
        entities.forEach(this::saveToXML);
    }

    /**
     * Save a map of clients to the repository
     *
     * @param entities : Map<ID, Client> to be saved to the repository
     * @throws XMLRepositoryClientException
     *          if some error from saveToXML method arises
     */
    public void saveMapToXML(Map<ID, Client> entities) throws XMLRepositoryClientException {
        entities.forEach((key, value) -> saveToXML(value));
    }

    /**
     * Converts a list of entities to a map of the form <ID,Entity>
     *
     * @param entities : List<Client> to be converted
     */
    public Map<ID, Client> convertListToMap(List<Client> entities){
        Map<ID, Client> map = new HashMap<>();
        entities.forEach(client -> map.put((ID) client.getId(), client));
        return map;
    }

    /**
     * Deletes all entries in the repository
     *
     * @throws XMLRepositoryClientException
     *         if some error from saveToXML method arises
     */
    public void deleteAll() throws XMLRepositoryClientException{
        List<Client> clients = this.loadFromXML();
        clients.forEach(client -> this.deleteClient((ID) client.getId()));
    }

    /**
     * Deletes an entry from the repository
     *
     * @param id : ID of the Client to be deleted
     * @throws XMLRepositoryClientException
     *         if some error occurs
     */
    public void deleteClient(ID id) throws XMLRepositoryClientException {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("data/xml/" + fileName + ".xml");

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//clients/client[id/text()=" + id + "]");

            Node clientNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            clientNode.getParentNode().removeChild(clientNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(
                    new DOMSource(document),
                    new StreamResult(new File("data/xml/" + fileName + ".xml"))
            );

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
            throw new XMLRepositoryClientException("failed to remove client.");
        }
    }

    @Override
    public Optional<T> findOne(ID id) {

        List<Client> entities = this.loadFromXML();
        Map<ID, Client> map = this.convertListToMap(entities);
        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        List<Client> entities = this.loadFromXML();
        Set<Client> allEntities = entities.stream().map(entry -> entry).collect(Collectors.toSet());
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
        List<Client> entities = this.loadFromXML();
        result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElse(saveToXML((Client)entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        List<Client> entities = this.loadFromXML();
        Map<ID, Client> map = this.convertListToMap(entities);

        Client result = map.remove(id);

        try {
            this.deleteClient(id);
        }catch(Exception e) {}

        return (Optional<T>) Optional.ofNullable(result);
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        List<Client> entities = this.loadFromXML();
        Map<ID, Client> map = this.convertListToMap(entities);


        Optional<T> result = (Optional<T>) entities.stream().filter(v -> v.getId() == entity.getId()).findAny();

        result.orElseGet(() -> {
            this.deleteClient(entity.getId());
            this.saveToXML((Client) entity);
            return null;
        });


        // fail to update? return entity
        return result;
    }
}

package DesignPattern.all;

interface NewParser {
    void parseXML();
}

class OldJsonParser {
    public void parseJson() {
        System.out.println("Parsing JSON data");
    }
}

class JsonToXmlAdapter implements NewParser {
    private OldJsonParser jsonParser;

    public JsonToXmlAdapter(OldJsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public void parseXML() {
        System.out.println("Adapting JSON to XML");
        jsonParser.parseJson();
    }
}

public class Adapter {
    public static void main(String[] args) {
        OldJsonParser jsonParser = new OldJsonParser();
        NewParser adapter = new JsonToXmlAdapter(jsonParser);
        adapter.parseXML();
    }
}
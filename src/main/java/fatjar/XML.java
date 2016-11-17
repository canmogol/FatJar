package fatjar;

import fatjar.implementations.xml.CurrentXML;

import java.util.Optional;

public interface XML {

    static XML create() {
        return XML.create(Type.JaxbXML);
    }

    static XML create(Type type) {
        return CurrentXML.create(type);
    }

    <T> Optional<T> fromXML(byte[] xmlData, Class<T> tClass);

    <T> Optional<String> toXML(T object);

    enum Type {
        JaxbXML, XStreamXML
    }

}

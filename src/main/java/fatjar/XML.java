package fatjar;

import fatjar.implementations.jaxb.JaxbXML;

import java.util.Optional;

public interface XML {

    static <T> Optional<T> fromXML(byte[] xmlData, Class<T> tClass) {
        return new JaxbXML().fromXML(xmlData, tClass);
    }

    static <T> Optional<String> toXML(Object object) {
        return new JaxbXML().toXML(object);
    }

}

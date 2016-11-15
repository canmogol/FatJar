package fatjar.implementations.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fatjar.Log;
import fatjar.XML;

public class JaxbXML implements XML {

    @SuppressWarnings("unchecked")
    public <T> Optional<T> fromXML(byte[] xmlData, Class<T> tClass) {
        try {
            return Optional.ofNullable((T) JAXBContext.newInstance(tClass).createUnmarshaller().unmarshal(new ByteArrayInputStream(xmlData)));
        } catch (Exception e) {
            Log.error("got exception while creating object from xml, exception: " + e);
            return Optional.empty();
        }
    }

    public <T> Optional<String> toXML(T object) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Marshaller marshaller = JAXBContext.newInstance(object.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(object, outStream);
            return Optional.of(new String(outStream.toByteArray()));
        } catch (Exception e) {
            Log.error("got exception while creating object from xml, exception: " + e);
            return Optional.empty();
        }
    }

}

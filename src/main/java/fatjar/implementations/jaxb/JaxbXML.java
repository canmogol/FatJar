package fatjar.implementations.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fatjar.XML;

public class JaxbXML implements XML {

	public <T> T fromXML(byte[] xmlData, Class<T> tClass) {

		try {
			return (T) JAXBContext.newInstance(tClass).createUnmarshaller().unmarshal(new ByteArrayInputStream(xmlData));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		// FIXME
		return null;

	}

	public String toXML(Object object) {
		try {

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			Marshaller marshaller = JAXBContext.newInstance(object.getClass()).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(object, outStream);

			return new String(outStream.toByteArray());
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		// FIXME
		return "N/A";
	}

}

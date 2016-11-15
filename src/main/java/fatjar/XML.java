package fatjar;

import fatjar.implementations.jaxb.JaxbXML;

public interface XML {

	static <T> T fromXML(byte[] xmlData, Class<T> tClass) {
		return new JaxbXML().fromXML(xmlData, tClass);
	}

	static String toXML(Object object) {
		return new JaxbXML().toXML(object);
	}

}

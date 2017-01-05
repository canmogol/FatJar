package fatjar.implementations.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import fatjar.Log;
import fatjar.XML;

import java.util.Optional;

public class XStreamXML implements XML {

    private final XStream xstream;

    public XStreamXML() {
	xstream = new XStream(new StaxDriver());
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> fromXML(byte[] xmlData, Class<T> tClass) {
	try {
	    return Optional.ofNullable((T) xstream.fromXML(new String(xmlData)));
	} catch (XStreamException e) {
	    Log.error("got exception while creating object from xml, exception: " + e, e);
	    return Optional.empty();
	}
    }

    public <T> Optional<String> toXML(T object) {
	try {
	    return Optional.ofNullable(xstream.toXML(object));
	} catch (XStreamException e) {
	    Log.error("got exception while creating object from xml, exception: " + e, e);
	    return Optional.empty();
	}
    }

}

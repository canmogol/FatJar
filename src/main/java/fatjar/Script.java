package fatjar;

import fatjar.implementations.script.CurrentScript;

import java.util.Map;

public interface Script {

    static Script create() {
        return Script.create(Type.JavaScript);
    }

    static Script create(Type type) {
        return CurrentScript.create(type);
    }

    Object evaluate(String content, Map<String, Object> map);

    Object evaluate(String content);

    enum Type {
        JavaScript, PythonScript, RubyScript
    }

}

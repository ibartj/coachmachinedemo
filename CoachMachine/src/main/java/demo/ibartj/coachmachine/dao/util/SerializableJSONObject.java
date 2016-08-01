package demo.ibartj.coachmachine.dao.util;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
@SuppressWarnings("unused")
public class SerializableJSONObject extends JSONObject implements Serializable {
    public SerializableJSONObject() {
        super();
    }

    public SerializableJSONObject(JSONObject jsonObject) throws org.json.JSONException {
        super(jsonObject.toString());
    }

    public SerializableJSONObject(java.util.Map copyFrom) {
        super(copyFrom);
    }

    public SerializableJSONObject(org.json.JSONTokener readFrom) throws org.json.JSONException {
        super(readFrom);
    }

    public SerializableJSONObject(String json) throws org.json.JSONException {
        super(json);
    }

    public SerializableJSONObject(JSONObject copyFrom, String[] names) throws org.json.JSONException {
        super(copyFrom, names);
    }

}

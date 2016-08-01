package demo.ibartj.coachmachine.dao.util;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
@SuppressWarnings("unused")
public class SerializableJSONArray extends JSONArray implements Serializable {
    public SerializableJSONArray() {
    }

    public SerializableJSONArray(JSONArray jsonArray) throws org.json.JSONException {
        super(jsonArray.toString());
    }

    public SerializableJSONArray(java.util.Collection copyFrom) {
        super(copyFrom);
    }

    public SerializableJSONArray(org.json.JSONTokener readFrom) throws org.json.JSONException {
        super(readFrom);
    }

    public SerializableJSONArray(String json) throws org.json.JSONException {
        super(json);
    }
}

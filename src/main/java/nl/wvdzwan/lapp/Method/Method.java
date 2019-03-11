package nl.wvdzwan.lapp.Method;

import java.util.HashMap;
import java.util.Map;

public abstract class Method {

    public final String namespace;
    public final String symbol;
    public final Map<String, String> metadata;


    protected Method(String namespace, String symbol) {
        this.namespace = namespace;
        this.symbol = symbol;

        this.metadata = new HashMap<>();
    }
}

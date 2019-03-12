package nl.wvdzwan.lapp.callgraph.outputs;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.DefaultAttribute;

import nl.wvdzwan.lapp.Method.Method;
import nl.wvdzwan.lapp.call.Call;
import nl.wvdzwan.lapp.call.ChaEdge;
import nl.wvdzwan.lapp.call.Edge;

public abstract class GraphVizOutput {
    private static Logger logger = LogManager.getLogger();

    protected final Graph<Method, Edge> graph;


    public GraphVizOutput(Graph<Method, Edge> graph) {
        this.graph = graph;
    }

    public boolean export(Writer writer) {

        DOTExporter<Method, Edge> exporter = new DOTExporter<>(
                this::vertexIdProvider,
                this::vertexLabelProvider,
                this::edgeLabelProvider,
                this::vertexAttributeProvider,
                this::edgeAttributeProvider);


        setGraphAttributes(exporter);

        exporter.exportGraph(graph, writer);

        return true;
    }

    protected void setGraphAttributes(DOTExporter<Method, Edge> exported) {
        // No graph attributes by default
    }

    abstract String vertexIdProvider(Method vertex);

    abstract String vertexLabelProvider(Method vertex);

    abstract Map<String, Attribute> vertexAttributeProvider(Method vertex);

    protected String edgeLabelProvider(Edge edge) {
        return edge.getLabel();
    }

    protected Map<String, Attribute> mapAsAttributeMap(Map<String, String> metadata) {
        Map<String, Attribute> result = new HashMap<>();


        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            result.put(
                    entry.getKey(),
                    DefaultAttribute.createAttribute(entry.getValue())
            );
        }

        return result;
    }


    protected Map<String, Attribute> edgeAttributeProvider(Edge edge) {

        if (edge instanceof Call) {
            return callAttributeProvider((Call) edge);
        } else if (edge instanceof ChaEdge) {
            return chaAttributeProvider((ChaEdge) edge);
        }

        return new HashMap<>();
    }

    private Map<String, Attribute> callAttributeProvider(Call call) {
        Map<String, Attribute> attributes = new HashMap<>();

        switch (call.callType) {

            case INTERFACE:

                attributes.put("style", DefaultAttribute.createAttribute("bold"));
                break;
            case VIRTUAL:
                attributes.put("style", DefaultAttribute.createAttribute("bold"));
                break;
            case SPECIAL:
                break;
            case STATIC:
                break;
            case UNKNOWN:
                break;
        }
        return attributes;
    }

    private Map<String, Attribute> chaAttributeProvider(ChaEdge edge) {
        Map<String, Attribute> attributes = new HashMap<>();

        switch (edge.type) {
            case IMPLEMENTS:
                attributes.put("style", DefaultAttribute.createAttribute("dashed"));
                break;

            case OVERRIDE:
                attributes.put("style", DefaultAttribute.createAttribute("dotted"));
                break;
        }

        return attributes;
    }
}

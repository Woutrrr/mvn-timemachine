package nl.wvdzwan.lapp.protobuf;

import java.util.stream.Collectors;

import nl.wvdzwan.lapp.call.Call;
import nl.wvdzwan.lapp.callgraph.ArtifactRecord;
import nl.wvdzwan.lapp.core.ClassRecord;
import nl.wvdzwan.lapp.core.ExpectedCall;
import nl.wvdzwan.lapp.core.LappPackage;
import nl.wvdzwan.lapp.core.Method;
import nl.wvdzwan.lapp.core.ResolvedMethod;

public class Protobuf {

    public static Lapp.Package of(LappPackage p) {
        return Lapp.Package.newBuilder()
                .addAllArtifacts(p.artifacts)
                .addAllResolvedCalls(p.resolvedCalls.stream().map(Protobuf::of).collect(Collectors.toList()))
                .addAllUnresolvedCalls(p.unresolvedCalls.stream().map(Protobuf::of).collect(Collectors.toList()))
                .addAllClassRecords(p.classRecords.stream().map(Protobuf::of).collect(Collectors.toList()))
                .build();
    }

    public static Lapp.Artifact of(ArtifactRecord artifact) {
        return Lapp.Artifact.newBuilder()
                .setGroup(artifact.groupId)
                .setName(artifact.artifactId)
                .setVersion(artifact.getVersion())
                .build();
    }

    public static Lapp.Method of(Method m) {
        Lapp.Method.Builder builder = Lapp.Method.newBuilder()
                .setNamespace(m.namespace)
                .setSymbol(m.symbol);

        if (m instanceof ResolvedMethod) {
            builder.setArtifact(((ResolvedMethod) m).artifact);
        }

        return builder.build();

    }

    public static Lapp.Call of(Call c) {
        return Lapp.Call.newBuilder()
                .setSource(of(c.source))
                .setTarget(of(c.target))
                .setCallType(of(c.callType))
                .build();

    }

    public static Lapp.Call.CallType of(Call.CallType c) {
        switch (c) {
            case INTERFACE:
                return Lapp.Call.CallType.INTERFACE;
            case VIRTUAL:
                return Lapp.Call.CallType.VIRTUAL;
            case SPECIAL:
                return Lapp.Call.CallType.SPECIAL;
            case STATIC:
                return Lapp.Call.CallType.STATIC;
            case RESOLVED_DISPATCH:
                return Lapp.Call.CallType.RESOLVED;
            case UNKNOWN:
                return Lapp.Call.CallType.UNKNOWN;
            default:
                return Lapp.Call.CallType.UNRECOGNIZED;
        }
    }

    public static Lapp.ExpectedCall of(ExpectedCall e) {
        return Lapp.ExpectedCall.newBuilder()
                .setSource(of(e.source))
                .setTarget(of(e.target))
                .build();
    }

    public static Lapp.ClassRecord of(ClassRecord r) {
        Lapp.ClassRecord.Builder builder = Lapp.ClassRecord.newBuilder()
                .setPackage(r.artifact)
                .setName(r.name)
                .addAllInterfaces(r.interfaces)
                .addAllMethods(r.methods)
                .addAllExpectedCalls(r.expectedCalls.stream().map(Protobuf::of).collect(Collectors.toList()))
                .setPublic(r.isPublic)
                .setPrivate(r.isPrivate)
                .setInterface(r.isInterface)
                .setAbstract(r.isAbstract);

        if (r.superClass != null) { // java/lang/Object doesn't have a super class
            builder.setSuperClass(r.superClass);
        }

        return builder.build();
    }
}

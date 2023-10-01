package jnetcall.java.api.flow;

public record MethodResult(short id, Object result, short status) implements Call{}
package jnetcall.java.api.flow;

public record MethodCall(short id, String className, String methodName, Object[] args) implements Call{}
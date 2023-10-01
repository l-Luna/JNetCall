package jnetbase.java.meta;

import java.lang.reflect.Method;

public record Property(String name, Method get, Method set) {}
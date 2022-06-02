package jnetcall.java.tests;

import jnetcall.java.client.ServiceClient;

import static jnetcall.java.client.ServiceEnv.buildPath;

public final class MainCallTest extends CallTest {

        final String Path =
                buildPath("..\\..\\..\\NET\\Alien1.Sharp\\bin\\Debug\\net6.0\\Alien1.Sharp.exe");

        @Override
        protected <T extends AutoCloseable> T create(Class<T> clazz) {
                return ServiceClient.createMain(clazz, Path);
        }
}

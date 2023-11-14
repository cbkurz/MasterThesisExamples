package de.kurz.ma.epsilon.runner;

import org.eclipse.epsilon.eol.EolModule;

public class Main {
    public static void main(String[] args) throws Exception {
        EolModule module = new EolModule();
        module.parse(Main.class.getClassLoader().getResource("HelloWorld.eol"));
        module.execute();
    }
}
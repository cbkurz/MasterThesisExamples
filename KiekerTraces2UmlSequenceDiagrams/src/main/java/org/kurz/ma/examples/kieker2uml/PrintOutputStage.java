package org.kurz.ma.examples.kieker2uml;

import teetime.framework.AbstractConsumerStage;

public class PrintOutputStage extends AbstractConsumerStage<Object> {

    protected void execute(Object element) throws Exception {
        System.out.println(String.format("(%s) %s", element.getClass(), element.toString()));
    }

}

package kieker.extension.performanceanalysis.epsilon;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

import static java.util.Objects.isNull;

public class EmfModelBuilder {
    private static final Path MODELS = Util.getResource("models");
    private final static Path META_MODELS = Util.getResource("metamodels");

    private final StringProperties properties;

    private EmfModelBuilder() {
        this.properties = new StringProperties();
        setDefaults();
    }

    private void setDefaults() {
        this.properties.setProperty(EmfModel.PROPERTY_READONLOAD, "true");
        this.properties.setProperty(EmfModel.PROPERTY_STOREONDISPOSAL, "true");
    }

    public static EmfModelBuilder getInstance() {
        return new EmfModelBuilder();
    }

    public EmfModelBuilder setReadOnLoad(final boolean readOnLoad) {
        this.properties.setProperty(EmfModel.PROPERTY_READONLOAD, Boolean.toString(readOnLoad));
        return this;
    }
    public EmfModelBuilder setStoreOnDisposal(final boolean storeOnDisposal) {
        this.properties.setProperty(EmfModel.PROPERTY_STOREONDISPOSAL, Boolean.toString(storeOnDisposal));
        return this;
    }
    public EmfModelBuilder setName(final String name) {
        this.properties.setProperty(EmfModel.PROPERTY_NAME, name);
        return this;
    }
    public EmfModelBuilder setAlias(final String alias) {
        this.properties.setProperty(EmfModel.PROPERTY_ALIASES, alias);
        return this;
    }
    public EmfModelBuilder setReadOnly(final boolean readOnly) {
        this.properties.setProperty(EmfModel.PROPERTY_READONLY, Boolean.toString(readOnly));
        return this;
    }
    public EmfModelBuilder setValidate(final boolean validate) {
        this.properties.setProperty(EmfModel.PROPERTY_VALIDATE, Boolean.toString(validate));
        return this;
    }

    public EmfModelBuilder setMetaModel(final URI metaModel) {
        this.properties.setProperty(EmfModel.PROPERTY_METAMODEL_URI, metaModel.toString());
        return this;
    }
    public EmfModelBuilder setMetaModel(final Path metaModel) {
        this.properties.setProperty(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI, metaModel.toAbsolutePath().toUri().toString());
        return this;
    }
    public EmfModelBuilder setMetaModel(final String metaModel) {
        this.properties.setProperty(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI, META_MODELS.resolve(metaModel).toAbsolutePath().toUri().toString());
        return this;
    }
    public EmfModelBuilder setReuseMetaModel(final boolean reuseMetaModel) {
        this.properties.setProperty(EmfModel.PROPERTY_REUSE_UNMODIFIED_FILE_BASED_METAMODELS, Boolean.toString(reuseMetaModel));
        return this;
    }

    public EmfModelBuilder setModel(final Path model) {
        this.properties.setProperty(EmfModel.PROPERTY_MODEL_URI, model.toAbsolutePath().toUri().toString());
        return this;
    }
    public EmfModelBuilder setModel(final String model) {
        this.properties.setProperty(EmfModel.PROPERTY_MODEL_URI, MODELS.resolve(model).toAbsolutePath().toUri().toString());
        return this;
    }


    public EmfModel build() {
        checkRequired();
        final EmfModel emfModel = new UmlModel();
        try {
            emfModel.load(this.properties);
        } catch (EolModelLoadingException e) {
            throw new RuntimeException(e);
        }
        return emfModel;
    }

    private void checkRequired() {
        Objects.requireNonNull(this.properties.get(EmfModel.PROPERTY_NAME), "A name for the model is required.");
        if (isNull(this.properties.get(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI))
                && isNull(this.properties.get(EmfModel.PROPERTY_METAMODEL_URI))) {
            throw new NullPointerException("A meta-model is required.");
        }
        Objects.requireNonNull(this.properties.get(EmfModel.PROPERTY_MODEL_URI));
    }
}

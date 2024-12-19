package network.aika.debug;


public class OutputConfig {

    private boolean showActivationFields = true;
    private boolean showLinkFields = true;
    private boolean showInputLinks = true;
    private boolean showFieldLinkProperties;


    public boolean isShowActivationFields() {
        return showActivationFields;
    }

    public OutputConfig setShowActivationFields(boolean showActivationFields) {
        this.showActivationFields = showActivationFields;
        return this;
    }

    public boolean isShowLinkFields() {
        return showLinkFields;
    }

    public OutputConfig setShowLinkFields(boolean showLinkFields) {
        this.showLinkFields = showLinkFields;
        return this;
    }

    public boolean isShowInputLinks() {
        return showInputLinks;
    }

    public OutputConfig setShowInputLinks(boolean showInputLinks) {
        this.showInputLinks = showInputLinks;
        return this;
    }

    public boolean isShowFieldLinkProperties() {
        return showFieldLinkProperties;
    }

    public OutputConfig setShowFieldLinkProperties(boolean showFieldLinkProperties) {
        this.showFieldLinkProperties = showFieldLinkProperties;
        return this;
    }
}

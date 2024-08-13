package rs.ac.uns.ftn.transport.model.enumerations;

public enum DocumentType {
    SAOBRACAJNA_DOZVOLA {
        public String toString() {
            return "Saobraćajna dozvola";
        }
    },
    VOZACKA_DOZVOLA {
        public String toString() {
            return "Vozačka dozvola";
        }
    };

    public static DocumentType getEnum(String name) {
        if (name.equalsIgnoreCase("Vozačka dozvola") || name.equalsIgnoreCase("Vozacka dozvola"))
            return VOZACKA_DOZVOLA;
        else if (name.equalsIgnoreCase("Saobraćajna dozvola") || name.equalsIgnoreCase("Saobracajna dozvola"))
            return SAOBRACAJNA_DOZVOLA;
        return null;
    }

    public static String getString(DocumentType type) {
        if (type == VOZACKA_DOZVOLA) {
            return "Vozačka dozvola";
        } else if (type == SAOBRACAJNA_DOZVOLA) {
            return "Saobraćajna dozvola";
        }
        return null;
    }
}

package com.perpcast.lib;


public final class FwiFoundation {

    /** AES Key size supported. */
     public enum FwiAesSize {
        k128(128),
        k192(192),
        k256(256);


        static public FwiAesSize getKey(int length) {
            switch (length) {
                case 192: return k192;
                case 256: return k256;
                default : return k128;
            }
        }


        public int length;
        FwiAesSize(int length) {
            this.length = length;
        }
    }

    /** Json supported. */
     public enum FwiJsonValue {
        kNull	 ((byte)0x00, "Null"   ), // Primitive
        kBoolean ((byte)0x01, "Boolean"), // Primitive
        kDouble	 ((byte)0x02, "Double" ), // Primitive
        kInteger ((byte)0x03, "Integer"), // Primitive
        kString	 ((byte)0x04, "String" ), // Primitive
        kArray	 ((byte)0x05, "Array"  ), // Constructed
        kObject	 ((byte)0x06, "Object" ); // Constructed

        public byte value = 0x00;
        public String description = null;
        FwiJsonValue(byte value, String description) {
            this.value = value; this.description = description;
        }

        @Override
        public String toString() { return description; }
    }

    /** HTTP request method supported. */
     public enum FwiHttpMethod {
        kCopy   ("COPY"),
        kDelete ("DELETE"),     // Delete data
        kGet    ("GET" ),       // Load data
        kHead   ("HEAD"),
        kLink   ("LINK"),
        kOptions("OPTIONS"),
        kPatch  ("PATCH"),
        kPost   ("POST"),       // Create data
        kPurge  ("PURGE"),
        kPut    ("PUT"),        // Update data
        kUnlink ("UNLINK");


        public String method = null;
        FwiHttpMethod(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return String.format("HTTP %s", this.method);
        }
    }
}

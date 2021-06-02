public  class Formatter {
    /** The format that is being build on */
    private byte[] mFormat;

    public Formatter() {
        // Default:
        mFormat = new byte[]{27, 33, 0};
    }

    /**
     * Method to get the Build result
     *
     * @return the format
     */
    public byte[] get() {
        return mFormat;
    }

    public Formatter bold() {
        // Apply bold:
        mFormat[2] = ((byte) (0x8 | mFormat[2]));
        return this;
    }

    public Formatter small() {
        mFormat[2] = ((byte) (0x1 | mFormat[2]));
        return this;
    }

    public Formatter height() {
        mFormat[2] = ((byte) (0x10 | mFormat[2]));
        return this;
    }

    public Formatter width() {
        mFormat[2] = ((byte) (0x20 | mFormat[2]));
        return this;
    }

    public Formatter underlined() {
        mFormat[2] = ((byte) (0x80 | mFormat[2]));
        return this;
    }

    public static byte[] rightAlign(){
        return new byte[]{0x1B, 'a', 0x02};
    }

    public static byte[] leftAlign(){
        return new byte[]{0x1B, 'a', 0x00};
    }

    public static byte[] centerAlign(){
        return new byte[]{0x1B, 'a', 0x01};
    }
}
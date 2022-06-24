/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package examples;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class DataRecordAvro extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7458622884281811282L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DataRecordAvro\",\"namespace\":\"examples\",\"fields\":[{\"name\":\"count\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<DataRecordAvro> ENCODER =
      new BinaryMessageEncoder<DataRecordAvro>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<DataRecordAvro> DECODER =
      new BinaryMessageDecoder<DataRecordAvro>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<DataRecordAvro> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<DataRecordAvro> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<DataRecordAvro> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<DataRecordAvro>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this DataRecordAvro to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a DataRecordAvro from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a DataRecordAvro instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static DataRecordAvro fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private long count;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public DataRecordAvro() {}

  /**
   * All-args constructor.
   * @param count The new value for count
   */
  public DataRecordAvro(java.lang.Long count) {
    this.count = count;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return count;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: count = (java.lang.Long)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'count' field.
   * @return The value of the 'count' field.
   */
  public long getCount() {
    return count;
  }


  /**
   * Sets the value of the 'count' field.
   * @param value the value to set.
   */
  public void setCount(long value) {
    this.count = value;
  }

  /**
   * Creates a new DataRecordAvro RecordBuilder.
   * @return A new DataRecordAvro RecordBuilder
   */
  public static examples.DataRecordAvro.Builder newBuilder() {
    return new examples.DataRecordAvro.Builder();
  }

  /**
   * Creates a new DataRecordAvro RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new DataRecordAvro RecordBuilder
   */
  public static examples.DataRecordAvro.Builder newBuilder(examples.DataRecordAvro.Builder other) {
    if (other == null) {
      return new examples.DataRecordAvro.Builder();
    } else {
      return new examples.DataRecordAvro.Builder(other);
    }
  }

  /**
   * Creates a new DataRecordAvro RecordBuilder by copying an existing DataRecordAvro instance.
   * @param other The existing instance to copy.
   * @return A new DataRecordAvro RecordBuilder
   */
  public static examples.DataRecordAvro.Builder newBuilder(examples.DataRecordAvro other) {
    if (other == null) {
      return new examples.DataRecordAvro.Builder();
    } else {
      return new examples.DataRecordAvro.Builder(other);
    }
  }

  /**
   * RecordBuilder for DataRecordAvro instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DataRecordAvro>
    implements org.apache.avro.data.RecordBuilder<DataRecordAvro> {

    private long count;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(examples.DataRecordAvro.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.count)) {
        this.count = data().deepCopy(fields()[0].schema(), other.count);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing DataRecordAvro instance
     * @param other The existing instance to copy.
     */
    private Builder(examples.DataRecordAvro other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.count)) {
        this.count = data().deepCopy(fields()[0].schema(), other.count);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'count' field.
      * @return The value.
      */
    public long getCount() {
      return count;
    }


    /**
      * Sets the value of the 'count' field.
      * @param value The value of 'count'.
      * @return This builder.
      */
    public examples.DataRecordAvro.Builder setCount(long value) {
      validate(fields()[0], value);
      this.count = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'count' field has been set.
      * @return True if the 'count' field has been set, false otherwise.
      */
    public boolean hasCount() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'count' field.
      * @return This builder.
      */
    public examples.DataRecordAvro.Builder clearCount() {
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataRecordAvro build() {
      try {
        DataRecordAvro record = new DataRecordAvro();
        record.count = fieldSetFlags()[0] ? this.count : (java.lang.Long) defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<DataRecordAvro>
    WRITER$ = (org.apache.avro.io.DatumWriter<DataRecordAvro>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<DataRecordAvro>
    READER$ = (org.apache.avro.io.DatumReader<DataRecordAvro>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeLong(this.count);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.count = in.readLong();

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.count = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










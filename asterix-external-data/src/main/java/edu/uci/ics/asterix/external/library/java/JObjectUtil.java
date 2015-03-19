/*
 * Copyright 2009-2013 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.external.library.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.asterix.common.exceptions.AsterixException;
import edu.uci.ics.asterix.dataflow.data.nontagged.serde.AInt32SerializerDeserializer;
import edu.uci.ics.asterix.dataflow.data.nontagged.serde.AStringSerializerDeserializer;
import edu.uci.ics.asterix.dataflow.data.nontagged.serde.SerializerDeserializerUtil;
import edu.uci.ics.asterix.external.library.java.JObjects.ByteArrayAccessibleDataInputStream;
import edu.uci.ics.asterix.external.library.java.JObjects.JBoolean;
import edu.uci.ics.asterix.external.library.java.JObjects.JCircle;
import edu.uci.ics.asterix.external.library.java.JObjects.JDate;
import edu.uci.ics.asterix.external.library.java.JObjects.JDateTime;
import edu.uci.ics.asterix.external.library.java.JObjects.JDouble;
import edu.uci.ics.asterix.external.library.java.JObjects.JDuration;
import edu.uci.ics.asterix.external.library.java.JObjects.JFloat;
import edu.uci.ics.asterix.external.library.java.JObjects.JInt;
import edu.uci.ics.asterix.external.library.java.JObjects.JInterval;
import edu.uci.ics.asterix.external.library.java.JObjects.JLine;
import edu.uci.ics.asterix.external.library.java.JObjects.JOrderedList;
import edu.uci.ics.asterix.external.library.java.JObjects.JPoint;
import edu.uci.ics.asterix.external.library.java.JObjects.JPoint3D;
import edu.uci.ics.asterix.external.library.java.JObjects.JPolygon;
import edu.uci.ics.asterix.external.library.java.JObjects.JRecord;
import edu.uci.ics.asterix.external.library.java.JObjects.JRectangle;
import edu.uci.ics.asterix.external.library.java.JObjects.JString;
import edu.uci.ics.asterix.external.library.java.JObjects.JTime;
import edu.uci.ics.asterix.external.library.java.JObjects.JUnorderedList;
import edu.uci.ics.asterix.om.types.AOrderedListType;
import edu.uci.ics.asterix.om.types.ARecordType;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.AUnionType;
import edu.uci.ics.asterix.om.types.AUnorderedListType;
import edu.uci.ics.asterix.om.types.BuiltinType;
import edu.uci.ics.asterix.om.types.EnumDeserializer;
import edu.uci.ics.asterix.om.types.IAType;
import edu.uci.ics.asterix.om.util.NonTaggedFormatUtil;
import edu.uci.ics.asterix.om.util.container.IObjectPool;
import edu.uci.ics.hyracks.api.exceptions.HyracksDataException;

public class JObjectUtil {

    public static IJObject getJType(ATypeTag typeTag, IAType type, ByteArrayAccessibleDataInputStream dis,
            IObjectPool<IJObject, IAType> objectPool) throws IOException, AsterixException {
        IJObject jObject;

        switch (typeTag) {

            case INT32: {
                int v = dis.readInt();
                jObject = objectPool.allocate(BuiltinType.AINT32);
                ((JInt) jObject).setValue(v);
                break;
            }

            case FLOAT: {
                float v = dis.readFloat();
                jObject = objectPool.allocate(BuiltinType.AFLOAT);
                ((JFloat) jObject).setValue(v);
                break;
            }

            case DOUBLE: {
                double value = dis.readDouble();
                jObject = objectPool.allocate(BuiltinType.ADOUBLE);
                ((JDouble) jObject).setValue(value);
                break;
            }

            case STRING: {
                String v = dis.readUTF();
                jObject = objectPool.allocate(BuiltinType.ASTRING);
                ((JString) jObject).setValue(v);
                break;
            }

            case BOOLEAN:
                jObject = objectPool.allocate(BuiltinType.ABOOLEAN);
                ((JBoolean) jObject).setValue(dis.readBoolean());
                break;

            case DATE: {
                int d = dis.readInt();
                jObject = objectPool.allocate(BuiltinType.ADATE);
                ((JDate) jObject).setValue(d);
                break;
            }

            case DATETIME: {
                jObject = objectPool.allocate(BuiltinType.ADATETIME);
                long value = dis.readLong();
                ((JDateTime) jObject).setValue(value);
                break;
            }

            case DURATION: {
                jObject = objectPool.allocate(BuiltinType.ADURATION);
                int months = dis.readInt();
                long msecs = dis.readLong();
                ((JDuration) jObject).setValue(months, msecs);
                break;
            }

            case TIME: {
                jObject = objectPool.allocate(BuiltinType.ATIME);
                int time = dis.readInt();
                ((JTime) jObject).setValue(time);
                break;
            }

            case INTERVAL: {
                jObject = objectPool.allocate(BuiltinType.AINTERVAL);
                long start = dis.readLong();
                long end = dis.readLong();
                byte intervalType = dis.readByte();
                ((JInterval) jObject).setValue(start, end, intervalType);
                break;
            }

            case CIRCLE: {
                jObject = objectPool.allocate(BuiltinType.ACIRCLE);
                double x = dis.readDouble();
                double y = dis.readDouble();
                double radius = dis.readDouble();
                JPoint jpoint = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                jpoint.setValue(x, y);
                ((JCircle) jObject).setValue(jpoint, radius);
                break;
            }

            case POINT: {
                jObject = objectPool.allocate(BuiltinType.APOINT);
                double x = dis.readDouble();
                double y = dis.readDouble();
                ((JPoint) jObject).setValue(x, y);
                break;
            }

            case POINT3D: {
                jObject = objectPool.allocate(BuiltinType.APOINT3D);
                double x = dis.readDouble();
                double y = dis.readDouble();
                double z = dis.readDouble();
                ((JPoint3D) jObject).setValue(x, y, z);
                break;
            }

            case LINE: {
                jObject = objectPool.allocate(BuiltinType.ALINE);
                double x1 = dis.readDouble();
                double y1 = dis.readDouble();
                double x2 = dis.readDouble();
                double y2 = dis.readDouble();
                JPoint jpoint1 = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                jpoint1.setValue(x1, y1);
                JPoint jpoint2 = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                jpoint2.setValue(x2, y2);
                ((JLine) jObject).setValue(jpoint1, jpoint2);
                break;
            }

            case POLYGON: {
                jObject = objectPool.allocate(BuiltinType.APOLYGON);
                short numberOfPoints = dis.readShort();
                List<JPoint> points = new ArrayList<JPoint>();
                for (int i = 0; i < numberOfPoints; i++) {
                    JPoint p1 = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                    p1.setValue(dis.readDouble(), dis.readDouble());
                    points.add(p1);
                }
                ((JPolygon) jObject).setValue(points);
                break;
            }

            case RECTANGLE: {
                jObject = objectPool.allocate(BuiltinType.ARECTANGLE);
                double x1 = dis.readDouble();
                double y1 = dis.readDouble();
                double x2 = dis.readDouble();
                double y2 = dis.readDouble();
                JPoint jpoint1 = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                jpoint1.setValue(x1, y1);
                JPoint jpoint2 = (JPoint) objectPool.allocate(BuiltinType.APOINT);
                jpoint2.setValue(x2, y2);
                ((JRectangle) jObject).setValue(jpoint1, jpoint2);
                break;
            }

            case UNORDEREDLIST: {
                AUnorderedListType listType = (AUnorderedListType) type;
                IAType elementType = listType.getItemType();
                jObject = objectPool.allocate(listType);

                boolean fixedSize = false;
                ATypeTag tag = EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(dis.readByte());
                switch (tag) {
                    case STRING:
                    case RECORD:
                    case ORDEREDLIST:
                    case UNORDEREDLIST:
                    case ANY:
                        fixedSize = false;
                        break;
                    default:
                        fixedSize = true;
                        break;
                }
                dis.readInt(); // list size
                int numberOfitems;
                numberOfitems = dis.readInt();
                if (numberOfitems > 0) {
                    if (!fixedSize) {
                        for (int i = 0; i < numberOfitems; i++)
                            dis.readInt();
                    }
                    for (int i = 0; i < numberOfitems; i++) {
                        IJObject v = (IJObject) getJType(elementType.getTypeTag(), elementType, dis, objectPool);
                        ((JUnorderedList) jObject).add(v);
                    }
                }

                break;
            }
            case ORDEREDLIST: {
                AOrderedListType listType = (AOrderedListType) type;
                IAType elementType = listType.getItemType();
                jObject = objectPool.allocate(listType);
                boolean fixedSize = false;
                ATypeTag tag = EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(dis.readByte());
                switch (tag) {
                    case STRING:
                    case RECORD:
                    case ORDEREDLIST:
                    case UNORDEREDLIST:
                    case ANY:
                        fixedSize = false;
                        break;
                    default:
                        fixedSize = true;
                        break;
                }

                dis.readInt(); // list size
                int numberOfitems;
                numberOfitems = dis.readInt();
                if (numberOfitems > 0) {
                    if (!fixedSize) {
                        for (int i = 0; i < numberOfitems; i++)
                            dis.readInt();
                    }
                    for (int i = 0; i < numberOfitems; i++) {
                        IJObject v = (IJObject) getJType(elementType.getTypeTag(), elementType, dis, objectPool);
                        ((JOrderedList) jObject).add(v);
                    }
                }

                break;
            }
            case RECORD:
                ARecordType recordType = (ARecordType) type;
                int numberOfSchemaFields = recordType.getFieldTypes().length;
                byte[] recordBits = dis.getInputStream().getArray();
                boolean isExpanded = false;
                int s = dis.getInputStream().getPosition();
                int recordOffset = s;
                int openPartOffset = 0;
                int offsetArrayOffset = 0;
                int[] fieldOffsets = new int[numberOfSchemaFields];
                IJObject[] closedFields = new IJObject[numberOfSchemaFields];

                if (recordType == null) {
                    openPartOffset = s + AInt32SerializerDeserializer.getInt(recordBits, s + 6);
                    s += 8;
                    isExpanded = true;
                } else {
                    dis.skip(4); // reading length is not required.
                    if (recordType.isOpen()) {
                        isExpanded = dis.readBoolean();
                        if (isExpanded) {
                            openPartOffset = s + dis.readInt(); // AInt32SerializerDeserializer.getInt(recordBits, s + 6);
                        } else {
                            // do nothing s += 6;
                        }
                    } else {
                        // do nothing s += 5;
                    }
                }

                if (numberOfSchemaFields > 0) {
                    int numOfSchemaFields = dis.readInt(); //s += 4;
                    int nullBitMapOffset = 0;
                    boolean hasNullableFields = NonTaggedFormatUtil.hasNullableField(recordType);
                    if (hasNullableFields) {
                        nullBitMapOffset = dis.getInputStream().getPosition();//s
                        offsetArrayOffset = dis.getInputStream().getPosition() //s
                                + (numberOfSchemaFields % 8 == 0 ? numberOfSchemaFields / 8
                                        : numberOfSchemaFields / 8 + 1);
                    } else {
                        offsetArrayOffset = dis.getInputStream().getPosition();
                    }
                    for (int i = 0; i < numberOfSchemaFields; i++) {
                        fieldOffsets[i] = dis.readInt(); // AInt32SerializerDeserializer.getInt(recordBits, offsetArrayOffset) + recordOffset;
                        // offsetArrayOffset += 4;
                    }
                    for (int fieldNumber = 0; fieldNumber < numberOfSchemaFields; fieldNumber++) {
                        if (hasNullableFields) {
                            byte b1 = recordBits[nullBitMapOffset + fieldNumber / 8];
                            int p = 1 << (7 - (fieldNumber % 8));
                            if ((b1 & p) == 0) {
                                // set null value (including type tag inside)
                                //fieldValues.add(nullReference);
                                continue;
                            }
                        }
                        IAType[] fieldTypes = recordType.getFieldTypes();
                        ATypeTag fieldValueTypeTag = null;

                        IAType fieldType = fieldTypes[fieldNumber];
                        if (fieldTypes[fieldNumber].getTypeTag() == ATypeTag.UNION) {
                            if (NonTaggedFormatUtil.isOptionalField((AUnionType) fieldTypes[fieldNumber])) {
                                fieldType = ((AUnionType) fieldTypes[fieldNumber]).getUnionList().get(
                                        AUnionType.OPTIONAL_TYPE_INDEX_IN_UNION_LIST);
                                fieldValueTypeTag = fieldType.getTypeTag();
                                //                      fieldValueLength = NonTaggedFormatUtil.getFieldValueLength(recordBits,
                                //                              fieldOffsets[fieldNumber], typeTag, false);
                            }
                        } else {
                            fieldValueTypeTag = fieldTypes[fieldNumber].getTypeTag();
                        }
                        closedFields[fieldNumber] = getJType(fieldValueTypeTag, fieldType, dis, objectPool);
                    }
                }
                if (isExpanded) {
                    int numberOfOpenFields = dis.readInt();
                    String[] fieldNames = new String[numberOfOpenFields];
                    IAType[] fieldTypes = new IAType[numberOfOpenFields];
                    IJObject[] openFields = new IJObject[numberOfOpenFields];
                    for (int i = 0; i < numberOfOpenFields; i++) {
                        dis.readInt();
                        dis.readInt();
                    }
                    for (int i = 0; i < numberOfOpenFields; i++) {
                        fieldNames[i] = AStringSerializerDeserializer.INSTANCE.deserialize(dis).getStringValue();
                        ATypeTag openFieldTypeTag = SerializerDeserializerUtil.deserializeTag(dis);
                        openFields[i] = getJType(openFieldTypeTag, null, dis, objectPool);
                        fieldTypes[i] = openFields[i].getIAObject().getType();
                    }
                    ARecordType openPartRecType = new ARecordType(null, fieldNames, fieldTypes, true);
                    if (numberOfSchemaFields > 0) {
                        ARecordType mergedRecordType = mergeRecordTypes(recordType, openPartRecType);
                        IJObject[] mergedFields = mergeFields(closedFields, openFields);
                        jObject = objectPool.allocate(recordType);
                        return new JRecord(mergedRecordType, mergedFields);
                    } else {
                        return new JRecord(recordType, openFields);
                    }
                } else {
                    return new JRecord(recordType, closedFields);
                }

            default:
                throw new IllegalStateException("Argument type: " + typeTag);
        }
        return jObject;
    }

    private static IJObject[] mergeFields(IJObject[] closedFields, IJObject[] openFields) {
        IJObject[] fields = new IJObject[closedFields.length + openFields.length];
        int i = 0;
        for (; i < closedFields.length; i++) {
            fields[i] = closedFields[i];
        }
        for (int j = 0; j < openFields.length; j++) {
            fields[closedFields.length + j] = openFields[j];
        }
        return fields;
    }

    private static ARecordType mergeRecordTypes(ARecordType recType1, ARecordType recType2) throws AsterixException {

        String[] fieldNames = new String[recType1.getFieldNames().length + recType2.getFieldNames().length];
        IAType[] fieldTypes = new IAType[recType1.getFieldTypes().length + recType2.getFieldTypes().length];

        int i = 0;
        for (; i < recType1.getFieldNames().length; i++) {
            fieldNames[i] = recType1.getFieldNames()[i];
            fieldTypes[i] = recType1.getFieldTypes()[i];
        }

        for (int j = 0; j < recType2.getFieldNames().length; i++, j++) {
            fieldNames[i] = recType2.getFieldNames()[j];
            fieldTypes[i] = recType2.getFieldTypes()[j];
        }
        try {
            return new ARecordType(null, fieldNames, fieldTypes, true);
        } catch (HyracksDataException e) {
            throw new AsterixException(e);
        }
    }
}

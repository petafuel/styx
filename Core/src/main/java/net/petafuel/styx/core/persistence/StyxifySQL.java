package net.petafuel.styx.core.persistence;

import org.postgresql.util.PGobject;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class StyxifySQL {

    private StyxifySQL() {
    }

    /**
     * Tries to map a ResultSet to a model/class by using the DatabaseColumn and DatabaseColumnOverride Annotations
     *
     * @param clazz
     * @param resultSet
     * @param overrides
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws SQLException
     */
    //
    //Data for reflected class is loaded from the database as trusted source
    @SuppressWarnings("squid:S1523")
    public static <T> T fetchModel(Class<T> clazz, ResultSet resultSet, DatabaseColumnOverride[] overrides) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, IntrospectionException {
        T templateInstance = clazz.getConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            Method setter = new PropertyDescriptor(field.getName(), templateInstance.getClass()).getWriteMethod();

            DatabaseColumn databaseColumnName = field.getAnnotation(DatabaseColumn.class);
            //Check if field should be mapped
            if (databaseColumnName != null) {
                if (databaseColumnName.nested()) {
                    //if nested type, use recursion to fill sub level class attributes
                    setter.invoke(templateInstance, fetchModel(field.getType(), resultSet, databaseColumnName.overrides()));
                } else {
                    //Check if there are overridden column names
                    Optional<DatabaseColumnOverride> optionalReplacement = Arrays.stream(overrides)
                            .filter(override -> databaseColumnName.value().equals(override.original()))
                            .findFirst();
                    //Use the overriden columnname if a replacement was specified
                    Object rowValue = optionalReplacement.isPresent() ?
                            resultSet.getObject(optionalReplacement.get().replacement())
                            : resultSet.getObject(databaseColumnName.value());

                    //Special case if postgres data type is represented as PGobject and its type is json
                    rowValue = checkComplexPostgresTypes(rowValue);
                    setter.invoke(templateInstance, rowValue);
                }
            }
        }
        return templateInstance;
    }

    public static <T> T fetchModel(Class<T> clazz, ResultSet resultSet) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, IntrospectionException {
        return fetchModel(clazz, resultSet, new DatabaseColumnOverride[0]);
    }

    private static Object checkComplexPostgresTypes(Object rowValue) {
        //PGobject json type cannot be cast to the necessary java type by static casting
        if (rowValue instanceof PGobject && "json".equals(((PGobject) rowValue).getType())) {
            rowValue = ((PGobject) rowValue).getValue();
        }
        return rowValue;
    }
}

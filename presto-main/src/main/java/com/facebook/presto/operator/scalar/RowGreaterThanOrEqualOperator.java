package com.facebook.presto.operator.scalar;
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.facebook.presto.metadata.BoundVariables;
import com.facebook.presto.metadata.FunctionManager;
import com.facebook.presto.spi.block.Block;
import com.facebook.presto.spi.type.RowType;
import com.facebook.presto.spi.type.Type;
import com.facebook.presto.spi.type.TypeManager;
import com.google.common.collect.ImmutableList;

import java.lang.invoke.MethodHandle;
import java.util.List;

import static com.facebook.presto.operator.scalar.ScalarFunctionImplementation.ArgumentProperty.valueTypeArgumentProperty;
import static com.facebook.presto.operator.scalar.ScalarFunctionImplementation.NullConvention.RETURN_NULL_ON_NULL;
import static com.facebook.presto.spi.function.OperatorType.GREATER_THAN;
import static com.facebook.presto.spi.function.OperatorType.GREATER_THAN_OR_EQUAL;
import static com.facebook.presto.util.Reflection.methodHandle;

public final class RowGreaterThanOrEqualOperator
        extends RowComparisonOperator
{
    public static final RowGreaterThanOrEqualOperator ROW_GREATER_THAN_OR_EQUAL = new RowGreaterThanOrEqualOperator();
    private static final MethodHandle METHOD_HANDLE = methodHandle(RowGreaterThanOrEqualOperator.class, "greaterOrEqual", RowType.class, List.class, Block.class, Block.class);

    private RowGreaterThanOrEqualOperator()
    {
        super(GREATER_THAN_OR_EQUAL);
    }

    @Override
    public ScalarFunctionImplementation specialize(BoundVariables boundVariables, int arity, TypeManager typeManager, FunctionManager functionManager)
    {
        Type type = boundVariables.getTypeVariable("T");
        return new ScalarFunctionImplementation(
                false,
                ImmutableList.of(
                        valueTypeArgumentProperty(RETURN_NULL_ON_NULL),
                        valueTypeArgumentProperty(RETURN_NULL_ON_NULL)),
                METHOD_HANDLE.bindTo(type).bindTo(getMethodHandles((RowType) type, functionManager, GREATER_THAN)),
                isDeterministic());
    }

    public static boolean greaterOrEqual(
            RowType rowType,
            List<MethodHandle> lessThanFunctions,
            Block leftRow, Block rightRow)
    {
        int compareResult = compare(rowType, lessThanFunctions, leftRow, rightRow);
        return compareResult >= 0;
    }
}

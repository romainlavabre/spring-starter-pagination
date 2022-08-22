#!/bin/bash

BASE_DIR="$1"
PACKAGE_PARSER=${BASE_DIR/"$2/src/main/java/com/"/""}
PACKAGES=""

IFS='/' read -ra ARRAY <<<"$PACKAGE_PARSER"
I=0

for PART in "${ARRAY[@]}"; do
    if [ "$I" == "0" ]; then
        PACKAGES="$PART"
    fi

    if [ "$I" == "1" ]; then
        PACKAGES="${PACKAGES}.${PART}"
    fi

    I=$((I + 1))
done

CLASSES=(
    "$1/Constraint.java"
    "$1/Pagination.java"
    "$1/PaginationHandler.java"
    "$1/PaginationHandlerImpl.java"
    "$1/query/Query.java"
    "$1/query/QueryBuilder.java"
    "$1/exception/NotSupportedKey.java"
    "$1/exception/NotSupportedOperator.java"
    "$1/exception/NotSupportedValue.java"
    "$1/condition/Condition.java"
    "$1/condition/ConditionBuilder.java"
    "$1/rt/RealTime.java"
    "$1/rt/RealTimeJpa.java"
)

for CLASS in "${CLASSES[@]}"; do
    sed -i "s|replace.replace|$PACKAGES|" "$CLASS"
done

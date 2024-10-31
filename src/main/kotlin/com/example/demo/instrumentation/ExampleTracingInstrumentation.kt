/*
 * Copyright 2021 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.instrumentation

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Example Instrumentation class that prints the time each datafetcher takes.
 */
@Component
class ExampleTracingInstrumentation: SimplePerformantInstrumentation() {

    val logger : Logger = LoggerFactory.getLogger(ExampleTracingInstrumentation::class.java)

    override fun createState(parameters: InstrumentationCreateStateParameters): InstrumentationState {
        return TraceState()
    }

    override fun beginExecution(parameters: InstrumentationExecutionParameters, state: InstrumentationState): InstrumentationContext<ExecutionResult>? {
        require(state is TraceState)
        state.traceStartTime = System.currentTimeMillis()

        return super.beginExecution(parameters, state)
    }

    override fun instrumentDataFetcher(dataFetcher: DataFetcher<*>, parameters: InstrumentationFieldFetchParameters, state: InstrumentationState): DataFetcher<*> {

        // We only care about user code
        if(parameters.isTrivialDataFetcher || parameters.executionStepInfo.path.toString().startsWith("/__schema")) {
            return dataFetcher
        }

        val dataFetcherName = findDatafetcherTag(parameters)

        return DataFetcher { environment ->
            val startTime = System.currentTimeMillis()
            val result = dataFetcher.get(environment)
            if(result is CompletableFuture<*>) {
                result.whenComplete { _,_ ->
                    val totalTime = System.currentTimeMillis() - startTime
                    logger.info("Async datafetcher '$dataFetcherName' took ${totalTime}ms")
                }
            } else {
                val totalTime = System.currentTimeMillis() - startTime
                logger.info("Datafetcher '$dataFetcherName': ${totalTime}ms")
            }

            result
        }
    }

    override fun instrumentExecutionResult(executionResult: ExecutionResult, parameters: InstrumentationExecutionParameters, state: InstrumentationState): CompletableFuture<ExecutionResult> {
        require(state is TraceState)
        val totalTime = System.currentTimeMillis() - state.traceStartTime
        logger.info("Total execution time: ${totalTime}ms")

        return super.instrumentExecutionResult(executionResult, parameters, state)
    }

    private fun findDatafetcherTag(parameters: InstrumentationFieldFetchParameters): String {
        val type = parameters.executionStepInfo.parent.type
        val parentType = if (type is GraphQLNonNull) {
            type.wrappedType as GraphQLObjectType
        } else {
            type as GraphQLObjectType
        }

        return "${parentType.name}.${parameters.executionStepInfo.path.segmentName}"
    }

    data class TraceState(var traceStartTime: Long = 0): InstrumentationState
}
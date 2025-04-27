package com.tuyetmai.newspaging


import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.io.FileInputStream
import java.io.IOException

class SentimentAnalyzer(context: Context) {

    private var interpreter: Interpreter? = null

    init {
        try {
            val assetFileDescriptor = context.assets.openFd("sentiment_model.tflite")
            val fileInputStream = assetFileDescriptor.createInputStream()
            val modelBytes = fileInputStream.readBytes()
            val byteBuffer = ByteBuffer.allocateDirect(modelBytes.size)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.put(modelBytes)

            interpreter = Interpreter(byteBuffer)
            fileInputStream.close()  // Close the input stream
            assetFileDescriptor.close()  // Close the asset file descriptor
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun analyze(text: String): String {
        // Vectorize the input text (convert the text into a numerical representation)
        val vectorizedText = vectorizeText(text)

        // Create tensor with the correct size (1, 256)
        val input = TensorBuffer.createFixedSize(intArrayOf(1, 256), org.tensorflow.lite.DataType.FLOAT32)
        input.loadArray(vectorizedText)  // Load the vectorized data into the input tensor

        // Define output tensor (assuming 2 classes for sentiment: Negative, Positive)
        val output = TensorBuffer.createFixedSize(intArrayOf(1, 2), org.tensorflow.lite.DataType.FLOAT32)

        // Run the model with the input tensor and get the output
        interpreter?.run(input.buffer, output.buffer)

        // Process the results
        val results = output.floatArray
        val maxIdx = results.indices.maxByOrNull { results[it] } ?: -1

        return when (maxIdx) {
            0 -> "Negative"
            1 -> "Positive"
            else -> "Unknown"
        }
    }

    fun vectorizeText(text: String): FloatArray {
        // Convert the text into a numerical vector (dummy example for 256 elements)
        return FloatArray(256) { 0.0f }  // Replace with actual vectorization logic
    }

    // Close the interpreter when no longer needed to free resources
    fun close() {
        interpreter?.close()
    }
}
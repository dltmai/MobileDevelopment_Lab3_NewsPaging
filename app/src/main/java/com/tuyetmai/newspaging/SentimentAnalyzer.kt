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
        // Giả sử text đã được vector hóa (hoặc tiền xử lý phù hợp)
        // Ở đây mình mock 1 tensor đầu vào mẫu

        // Preprocess the text to convert it into a tensor
        val input = TensorBuffer.createFixedSize(intArrayOf(1, 20), org.tensorflow.lite.DataType.FLOAT32)
        input.loadArray(FloatArray(20) { 0.0f }) // Dummy data: replace this with actual text vectorization

        // Define output tensor
        val output = TensorBuffer.createFixedSize(intArrayOf(1, 3), org.tensorflow.lite.DataType.FLOAT32)
        interpreter?.run(input.buffer, output.buffer)

        // Extract results
        val results = output.floatArray
        val maxIdx = results.indices.maxByOrNull { results[it] } ?: -1

        return when (maxIdx) {
            0 -> "Negative"
            1 -> "Neutral"
            2 -> "Positive"
            else -> "Unknown"
        }
    }

    // Close the interpreter when no longer needed to free resources
    fun close() {
        interpreter?.close()
    }
}
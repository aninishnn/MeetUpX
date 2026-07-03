package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun QrCodeGenerator(
    referenceCode: String,
    modifier: Modifier = Modifier,
    qrColor: Color = Color(0xFF0F172A), // Slate 900
    backgroundColor: Color = Color.White
) {
    val gridSize = 21
    val seed = remember(referenceCode) {
        referenceCode.hashCode().toLong()
    }

    val grid = remember(seed) {
        val random = Random(seed)
        val matrix = Array(gridSize) { BooleanArray(gridSize) }

        markFinderPattern(matrix, 0, 0)
        markFinderPattern(matrix, gridSize - 7, 0)
        markFinderPattern(matrix, 0, gridSize - 7)

        for (i in 8 until gridSize - 8) {
            matrix[6][i] = i % 2 == 0
            matrix[i][6] = i % 2 == 0
        }

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (isInsideFinder(r, c, gridSize)) continue
                if (r == 6 || c == 6) continue // Timing lines

                // Random block
                matrix[r][c] = random.nextBoolean()
            }
        }
        matrix
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(2.dp, qrColor.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val moduleSize = size.width / gridSize

            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    if (grid[r][c]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * moduleSize, r * moduleSize),
                            size = Size(moduleSize + 0.5f, moduleSize + 0.5f)
                        )
                    }
                }
            }

            drawFinderOverlays(this, 0f, 0f, moduleSize)
            drawFinderOverlays(this, (gridSize - 7) * moduleSize, 0f, moduleSize)
            drawFinderOverlays(this, 0f, (gridSize - 7) * moduleSize, moduleSize)
        }
    }
}

private fun markFinderPattern(matrix: Array<BooleanArray>, rowOffset: Int, colOffset: Int) {
    for (r in 0..6) {
        for (c in 0..6) {
            val isBorder = r == 0 || r == 6 || c == 0 || c == 6
            val isCenter = r in 2..4 && c in 2..4
            matrix[rowOffset + r][colOffset + c] = isBorder || isCenter
        }
    }
}

private fun isInsideFinder(r: Int, c: Int, gridSize: Int): Boolean {
    if (r < 8 && c < 8) return true // Top-left
    if (r < 8 && c > gridSize - 9) return true // Top-right
    if (r > gridSize - 9 && c < 8) return true // Bottom-left
    return false
}

private fun drawFinderOverlays(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    x: Float,
    y: Float,
    moduleSize: Float
) {
    val sizePx = moduleSize * 7
    // Outer solid square
    drawScope.drawRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(x, y),
        size = Size(sizePx, sizePx)
    )
    // Inner white hollow cutout
    val hollowOffset = moduleSize
    val hollowSize = moduleSize * 5
    drawScope.drawRect(
        color = Color.White,
        topLeft = Offset(x + hollowOffset, y + hollowOffset),
        size = Size(hollowSize, hollowSize)
    )
    // Inner solid core
    val coreOffset = moduleSize * 2
    val coreSize = moduleSize * 3
    drawScope.drawRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(x + coreOffset, y + coreOffset),
        size = Size(coreSize, coreSize)
    )
}

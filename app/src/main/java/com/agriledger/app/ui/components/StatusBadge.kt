package com.agriledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.SuccessGreen
import com.agriledger.app.ui.theme.SuccessGreenBg
import com.agriledger.app.ui.theme.WarningGold
import com.agriledger.app.ui.theme.WarningGoldBg

@Composable
fun StatusBadge(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, dotColor) = when (status) {
        PaymentStatus.PAID -> Triple(SuccessGreenBg, SuccessGreen, SuccessGreen)
        PaymentStatus.PARTIAL -> Triple(WarningGoldBg, WarningGold, WarningGold)
        PaymentStatus.UNPAID -> Triple(ErrorCrimsonBg, ErrorCrimson, ErrorCrimson)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 11.sp
                )
            )
        }
    }
}

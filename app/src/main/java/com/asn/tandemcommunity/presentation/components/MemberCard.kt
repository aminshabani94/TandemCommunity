package com.asn.tandemcommunity.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.domain.model.CommunityMember
import com.asn.tandemcommunity.presentation.preview.previewCommunityMemberExisting
import com.asn.tandemcommunity.presentation.preview.previewCommunityMemberNew
import com.asn.tandemcommunity.presentation.theme.CommunityScreenPreviewTheme
import com.asn.tandemcommunity.presentation.theme.Dimens
import com.asn.tandemcommunity.presentation.theme.TandemTextPrimary
import com.asn.tandemcommunity.presentation.theme.TandemTextSecondary
import com.asn.tandemcommunity.presentation.theme.TandemTextTertiary

@Composable
fun MemberCard(
    member: CommunityMember,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onLikeClick)
            .padding(
                horizontal = Dimens.screenPaddingHorizontal,
                vertical = Dimens.cardPaddingVertical,
            ),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = member.pictureUrl,
            contentDescription = member.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimens.profileImageSize)
                .clip(RoundedCornerShape(Dimens.profileImageCorner))
        )
        Spacer(Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .height(Dimens.profileImageSize)
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TandemTextPrimary
                )
                if (member.isNew) {
                    NewBadge()
                } else {
                    Text(
                        text = member.referenceCount.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TandemTextPrimary
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = member.topic,
                style = MaterialTheme.typography.bodyMedium,
                color = TandemTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    LanguageLabel(
                        label = stringResource(R.string.label_native),
                        languages = member.nativeLanguages,
                    )
                    Spacer(Modifier.width(Dimens.screenPaddingHorizontal))
                    LanguageLabel(
                        label = stringResource(R.string.label_learns),
                        languages = member.learnsLanguages,
                    )
                }
                LikeButton(
                    isLiked = isLiked,
                    onClick = onLikeClick,
                    interactive = false,
                )
            }
        }
    }
}

@Composable
private fun LanguageLabel(label: String, languages: List<String>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = TandemTextPrimary
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = languages.joinToString(" ").uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
            ),
            color = TandemTextTertiary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MemberCardPreview(
    @PreviewParameter(MemberCardPreviewProvider::class) member: CommunityMember
) {
    CommunityScreenPreviewTheme(modifier = Modifier.fillMaxWidth()) {
        MemberCard(
            member = member,
            isLiked = member.isLiked,
            onLikeClick = {},
        )
    }
}

class MemberCardPreviewProvider : PreviewParameterProvider<CommunityMember> {
    override val values = sequenceOf(
        previewCommunityMemberNew,
        previewCommunityMemberExisting
    )

    override fun getDisplayName(index: Int): String = when (index) {
        0 -> "Member – new"
        else -> "Member – existing"
    }
}

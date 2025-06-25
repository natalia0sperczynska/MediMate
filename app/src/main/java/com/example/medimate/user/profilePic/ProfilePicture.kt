import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.healme.R

/**
 * Displays a profile picture from a URL or a placeholder icon if no URL is provided.
 *
 * @param profilePictureUrl The URL of the profile picture image. If null or empty, the placeholder is shown.
 * @param modifier Modifier to be applied to the Image composable.
 * @param size The size of the profile picture (width and height).
 * @param placeholder A composable lambda that provides a placeholder UI when no profile picture is available.
 */
@Composable
fun ProfilePicture(
    profilePictureUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    placeholder: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default profile",
            modifier = Modifier.size(size)
        )
    }
) {
    if (!profilePictureUrl.isNullOrEmpty()) {
        Image(
            painter = rememberImagePainter(
                data = profilePictureUrl,
                builder = {
                    placeholder(R.drawable.profile_pic)
                    error(R.drawable.profile_pic)
                }
            ),
            contentDescription = "Profile picture",
            modifier = modifier.size(size)
        )
    } else {
        placeholder()
    }
}
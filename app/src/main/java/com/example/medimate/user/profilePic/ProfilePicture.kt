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
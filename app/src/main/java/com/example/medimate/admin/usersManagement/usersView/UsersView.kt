package com.example.medimate.admin.usersManagement.usersView

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleDark
import com.example.medimate.ui.theme.White
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun SingleUser(user: User, isSelected: Boolean, onUserSelected: (String) -> Unit, navController: NavController) {
    var expand by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(targetValue = if(expand) 40.dp else 0.dp, label = "")
    Surface(border = BorderStroke(1.dp, White), color = PurpleDark,
        shape= MaterialTheme.shapes.medium){
        Row (modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(bottom = extraPadding)) {
                Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = null, modifier = Modifier. requiredSize(50.dp))
                Text(text = "${user.name}  ${user.surname}",color=White)
                Text(user.dateOfBirth,color=White)
                if (expand) {
                    Text("e-mail: ${user.email}",color=White)
                    Text("phone number: ${user.phoneNumber}",color=White)
                    Text("address: ${user.address}",color=White)
                }
            }
            Column(modifier = Modifier.selectableGroup()) {
                OutlinedButton(onClick = { expand = !expand }) {
                    Text(
                        if (expand) "Show less" else "Show more",
                        color = White
                    )
                }
                OutlinedButton(onClick = {onUserSelected(user.id)
                    navController.navigate(Screen.UserDocumentation.createRoute(user.id))}) {
                    Text(text = "See the documentation",
                        color = White
                    )
                }
                OutlinedButton(onClick = {onUserSelected(user.id)
                    navController.navigate(Screen.EditUserData.createRoute(user.id))}) {
                    Text(text = "Edit user data",
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
fun UserList(users: List<User>, navController: NavController) {
    var selectedUserId: String? by remember { mutableStateOf(null) }
    var selectedUser : User? by
    remember { mutableStateOf(null) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (users.isEmpty()) {
            Text("No users available")
        } else
            LazyColumn {
                item{Text("Select a user:", color = White)}
                items(users){ user ->
                    SingleUser(user = user, isSelected = (selectedUserId == user.id),
                        onUserSelected = { id -> selectedUserId = id }, navController = navController)
                }
            }
    }

}
class MainUserViewModel: ViewModel() {
    private val _searchText = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _users = MutableStateFlow<List<User>>(emptyList())

    val searchText = _searchText.asStateFlow()
    val isSearching = _isSearching.asStateFlow()
    val users = _users.asStateFlow()

    init {
        loadUsers()
        setupSearch()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _isSearching.value = true
            _users.value = UserDAO().getAllUsers()
            _isSearching.value = false
        }
    }

    private fun setupSearch() {
        viewModelScope.launch {
            searchText
                .debounce(500L)
                .collect { query ->
                    _isSearching.value = true
                    val allUsers = UserDAO().getAllUsers() // Get fresh list
                    val filtered = if (query.isBlank()) {
                        allUsers
                    } else {
                        allUsers.filter { it.doesMatchSearchQuery(query) }
                    }
                    _users.value = filtered
                    _isSearching.value = false
                }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

suspend fun getUserList():List<User>{
    val mFireBase = UserDAO()
    val users = mFireBase.getAllUsers()
    return users
}

@Composable
fun UsersScreen(navController: NavController) {
    val viewModel = viewModel<MainUserViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val person by viewModel.users!!.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModelNavDrawerAdmin(navController, drawerState, scope = scope) {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchUserBar(modifier = Modifier.fillMaxWidth(), viewModel = viewModel, searchText)
            Spacer(modifier = Modifier.height(16.dp))
            MediMateButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.navigate(Screen.MainUser.route)},
                text="Go back")
            Spacer(modifier = Modifier.height(16.dp))
            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                UserList(users = person, navController = navController)
            }
        }
    }
}

@Composable
fun SearchUserBar(
    modifier: Modifier = Modifier,
    viewModel: MainUserViewModel = viewModel(),
    searchText: String
){
    TextField( value=searchText, onValueChange = viewModel::onSearchTextChange, trailingIcon = {
        Icon(Icons.Default.Search, contentDescription = null)
    }, placeholder = { Text(stringResource(id=R.string.place_holder_search_user)) },
        modifier = modifier
            .heightIn(min=56.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp))

}

@Preview(showSystemUi = true)
@Composable
fun UserViewPreview() {
    MediMateTheme {
       UsersScreen(navController = rememberNavController())
    }
    //dto,, przekazac tylko dane wyswietlane
}

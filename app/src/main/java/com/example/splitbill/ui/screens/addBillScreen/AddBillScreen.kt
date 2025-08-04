package com.example.splitbill.ui.screens.addBillScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.navigation.HomeRoute
import com.example.splitbill.ui.components.MyAppButton
import com.example.splitbill.ui.screens.addBillScreen.AddBillIntent.EnterTitle
import com.example.splitbill.ui.screens.mainScreen.MainIntent

@Composable
fun AddBillScreenRoot(
    viewModel: AddBillViewModel,
    navController: NavController
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(AddBillIntent.FetchFriends)

        viewModel.effect.collect { effect ->
            when (effect) {
                AddBillEffect.NavigateToBillScreen -> navController.navigate(HomeRoute)
            }
        }
    }

    AddBillScreen(
        state = state,
        onEvent = viewModel::handleIntent,
        onNavigateHome = {
            navController.navigate(HomeRoute)
        }
    )
}
@Composable
fun AddBillScreen(
    state: AddBillUiState,
    onEvent: (AddBillIntent) -> Unit,
    onNavigateHome: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back icon",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        onNavigateHome()
                    },
                tint = Color.Black
            )

            Text(
                text = "Create a new split bill",
                fontSize = 18.sp,
            )

        }


        Spacer(modifier = Modifier.height(16.dp))

        if( state.friends.isEmpty()) {
            Text(
                text = "You dont have any friends yet. " +
                        "Add some friends to be able to create a bill.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            return
        }

        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(EnterTitle(it)) },
            label = { Text("Bill Name") },
            isError = state.billNameError != null,
            supportingText = state.billNameError?.let {
                { Text(text = it, color = Color.Red) }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        state.participantsError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        FriendsList(
            friends = state.friends,
            selectedFriends = state.selectedFriendIds,
            onFriendSelected = { friend ->
                onEvent(AddBillIntent.ToggleParticipant(friend.id))
            },
            modifier = Modifier
                .weight(1f)
        )

        MyAppButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, shape = RoundedCornerShape(20.dp)),
            text = "Save Bill",
            onClick = { onEvent(AddBillIntent.SaveBill) }
        )
    }
}

@Composable
fun FriendsList(
    friends: List<Friend>,
    selectedFriends: Set<Long>,
    onFriendSelected: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        friends.forEach { friend ->
            val isSelected = friend.id in selectedFriends
            FriendItem(
                friend = friend,
                isSelected = isSelected,
                onClick = {
                    onFriendSelected(friend)
                }
            )
        }
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE0F7FA) else Color.White
    val borderColor = if (isSelected) Color(0xFF00838F) else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Avatar placeholder or image
            if (friend.avatarUri != null) {
                AsyncImage(
                    model = friend.avatarUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = friend.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color(0xFF006064) else Color.Black
            )

            Spacer(Modifier.weight(1f))

            // Checkmark for selected state
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF006064)
                )
            }
        }
    }
}

@Preview
@Composable
fun AddBillScreenPreview() {
    val sampleFriends = listOf(
        Friend(id = 1, name = "Alice", avatarUri = null),
        Friend(id = 2, name = "Bob", avatarUri = null),
        Friend(id = 3, name = "Charlie", avatarUri = null)
    )

    AddBillScreen(
        state = AddBillUiState(
            title = "Wakacje",
            friends = sampleFriends,
            selectedFriendIds = setOf(1, 2)
        ),
        onEvent = {},
        onNavigateHome = { }
    )
}


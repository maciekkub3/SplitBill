package com.example.splitbill.ui.Screens.MainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbill.ui.components.MyAppButton
import com.example.splitbill.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.splitbill.data.classes.BillWithParticipantCount
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.navigation.AddEventRoute


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController,
    ) {

    LaunchedEffect(Unit) {
        viewModel.handleIntent(MainIntent.FetchFriends)
        viewModel.handleIntent(MainIntent.FetchBills)

        viewModel.effect.collect { effect ->
            when (effect) {
                MainEffect.NavigateToBillScreen -> {
                    navController.navigate(AddEventRoute)
                }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (state.addFriendDialog) {
                AddFriendDialog(
                    name = state.friendName,
                    onNameChange = { viewModel.handleIntent(MainIntent.OnNewFriendNameChange(it)) },
                    onAddClick = { viewModel.handleIntent(MainIntent.AddFriend) },
                    onCancelClick = { viewModel.handleIntent(MainIntent.CloseAddFriendDialog) },
                    onDismissRequest = { viewModel.handleIntent(MainIntent.CloseAddFriendDialog) },
                )
        } else if (state.editFriendDialog) {
                AddFriendDialog(
                    name = state.friendName,
                    onNameChange = { viewModel.handleIntent(MainIntent.OnNewFriendNameChange(it)) },
                    onAddClick = { viewModel.handleIntent(MainIntent.EditFriend(state.editingFriendId, state.friendName))},
                    onCancelClick = { viewModel.handleIntent(MainIntent.CloseEditFriendDialog) },
                    onDismissRequest = { viewModel.handleIntent(MainIntent.CloseEditFriendDialog) },
                    isEditMode = true
                )
        }


        Column(
            modifier = Modifier
                .background(color = Color(0xFFE0E0E0))
                .fillMaxSize()
        ) {
            Text(
                text = "Home",
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalCardRow(
                bills = state.bills
            )

            MyAppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                text = "Add Bill",
                onClick = {
                    viewModel.handleIntent(MainIntent.OnAddBillClicked)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(color = Color.White)
                    .fillMaxSize()
                    .padding(16.dp),
            ){
                Text(
                    text = "Friends",
                    fontSize = 30.sp,
                )

                ScrollingGrid(
                    friends = state.friends,
                    onFriendClick = { friend ->
                        viewModel.handleIntent(MainIntent.OpenEditFriendDialog(friend))
                    }
                )

                MyAppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    text = "Add Friend",
                    onClick = { viewModel.handleIntent(MainIntent.OpenAddFriendDialog) }
                )
            }
        }
    }
}

/*@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}*/

@Composable
fun AddFriendDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    isEditMode: Boolean = false,
    ) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {

                        Text( if (isEditMode) "Save" else "Add", color = Color.White)
                    }

                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text( if (isEditMode) "Delete" else "Cancel", color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun ScrollingGrid(
    friends: List<Friend>,
    onFriendClick: (Friend) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .height(300.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(friends) { friend ->
            FriendBox(
                name = friend.name,
                onClick = { onFriendClick(friend) }
            )
        }
    }
}

@Composable
fun HorizontalCardRow(bills: List<BillWithParticipantCount>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bills) { bill ->
            BillBox(bill = bill)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun BillBox(bill: BillWithParticipantCount) {
    Card(
        modifier = Modifier.size(width = 160.dp, height = 120.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = bill.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Created: ${java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(bill.createdAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${bill.participantCount} participant${if (bill.participantCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BillBoxPreview() {
    val sampleBill = BillWithParticipantCount(
        id = 1L,
        title = "Dinner Party",
        createdAt = System.currentTimeMillis(),
        participantCount = 5
    )
    BillBox(bill = sampleBill)
}

@Composable
fun FriendBox(
    name: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(130.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE0E0E0))
            .padding(10.dp)
            .clickable {onClick()},
    ) {
        Image(
            painter = painterResource(id = R.drawable.icons8_user),
            contentDescription = "Friend Icon",
            modifier = Modifier
                .size(80.dp)
        )
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
fun previewFriendBox() {
    FriendBox(
        name = "John Doe",
        onClick = {}
    )
}

/*@Preview
@Composable
fun previewBillBox() {
    BillBox(
        value = 100.0,
        participants = 5
    )
}*/

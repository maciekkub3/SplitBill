package com.example.splitbill.ui.screens.mainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.splitbill.data.classes.BillWithParticipantCount
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.navigation.AddEventRoute
import com.example.splitbill.navigation.EventRoute
import com.example.splitbill.ui.screens.billScreen.DeleteDialog

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()

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

    MainScreen(
        state = state,
        onEvent = viewModel::handleIntent,
        onBillClick = { billId ->
            navController.navigate(EventRoute(billId))
        }
    )
}
@Composable
fun MainScreen(
    state: MainUiState,
    onEvent: (MainIntent) -> Unit,
    onBillClick: (Long) -> Unit,
    ) {

    if (state.showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { onEvent(MainIntent.DismissDeleteDialog) },
            onConfirm = {
                onEvent(MainIntent.DeleteFriend(Friend(state.editingFriendId!!, state.friendName)))
            },
            title = "Delete Friend",
            message = "Are you sure you want to delete this friend?",
            icon = Icons.Default.Clear
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (state.addFriendDialog) {
                AddFriendDialog(
                    name = state.friendName,
                    onNameChange = { onEvent(MainIntent.OnNewFriendNameChange(it)) },
                    onAddClick = { onEvent(MainIntent.AddFriend) },
                    onCancelClick = { onEvent(MainIntent.CloseAddFriendDialog) },
                    onDismissRequest = { onEvent(MainIntent.CloseAddFriendDialog) },
                )
        } else if (state.editFriendDialog) {
                AddFriendDialog(
                    name = state.friendName,
                    onNameChange = { onEvent(MainIntent.OnNewFriendNameChange(it)) },
                    onAddClick = { onEvent(MainIntent.EditFriend(state.editingFriendId, state.friendName))},
                    onCancelClick = { onEvent(MainIntent.CloseEditFriendDialog) },
                    onDismissRequest = { onEvent(MainIntent.CloseEditFriendDialog) },
                    isEditMode = true,
                    onDeleteClick = { onEvent(MainIntent.ShowDeleteDialog) }
                )
        }

        Column(
            modifier = Modifier
                .background(color = Color(0xFFE0E0E0))
                .fillMaxSize()
        ) {
            Text(
                text = "Dashboard",
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(24.dp)
            )

            HorizontalCardRow(
                bills = state.bills,
                onBillClick = onBillClick,
                onAddBillClick = {
                    onEvent(MainIntent.OnAddBillClicked)
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

                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                ) {
                    if (state.friends.isEmpty()) {
                        Text(
                            text = "No friends added yet.",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            fontSize = 20.sp,
                            color = Color.Gray,
                        )
                    } else {
                        ScrollingGrid(
                            friends = state.friends,
                            onFriendClick = { friend ->
                                onEvent(MainIntent.OpenEditFriendDialog(friend))
                            }
                        )
                    }
                }

                MyAppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, shape = RoundedCornerShape(20.dp)),
                    text = "Add Friend",
                    onClick = { onEvent(MainIntent.OpenAddFriendDialog) }
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
    onDismissRequest: () -> Unit,
    isEditMode: Boolean = false,
    onDeleteClick: () -> Unit = {}
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier.size(width = 100.dp, height = 45.dp),
                    ) {

                        Text( if (isEditMode) "Save" else "Add", color = Color.White)
                    }

                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.size(width = 100.dp, height = 45.dp)

                    ) {
                        Text( "Cancel", color = Color.White)
                    }
                }
            }
            if (isEditMode) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    tint = Color.Red,
                    contentDescription = "Edit Friend",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.TopEnd)
                        .clickable { onDeleteClick() }
                )
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
fun HorizontalCardRow(bills: List<BillWithParticipantCount>, onBillClick: (Long) -> Unit, onAddBillClick: () -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        item {
            VerticalAddBillButton(
                onClick = onAddBillClick
            )
        }

        if (bills.isEmpty()) {
            item {
                Text(
                    text = "No bills added yet.",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        items(bills) { bill ->
            BillBox(
                bill = bill,
                onBillClick = onBillClick
            )
        }
    }
}

@Composable
fun VerticalAddBillButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(220.dp)
            .width(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Cyan),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(top = 16.dp)
        ) {
            Text(
                text = "Add Bill",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.rotate(-90f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.border(width = 1.dp, color = Color.White, shape = CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun VerticalAddBillButtonPreview() {
    VerticalAddBillButton(onClick = {})
}

@SuppressLint("SimpleDateFormat", "DefaultLocale")
@Composable
fun BillBox(bill: BillWithParticipantCount, onBillClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 220.dp)
            .clickable { onBillClick(bill.id) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFADEED9))
    ) {
        Box(
            modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Column() {
                    Text(
                        text = bill.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "$ ${String.format("%.2f", bill.totalAmount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column() {
                    Text(
                        text = "Split to",
                        fontSize = 22.sp
                    )
                    Text(
                        text = "${bill.participantCount} ${if (bill.participantCount == 1) "person" else "people"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Text(
                text = "${java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(bill.createdAt))}",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
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
        participantCount = 5,
        totalAmount = 150.0
    )
    Box(modifier = Modifier.background(Color.Black)) {
        BillBox(bill = sampleBill, onBillClick = {})
    }
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
                .size(70.dp)
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
fun PreviewFriendBox() {
    FriendBox(
        name = "John Doe",
        onClick = {}
    )
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        state = MainUiState(
            isLoading = false,
            bills = listOf(
                BillWithParticipantCount(1L, "Dinner", System.currentTimeMillis(), 3, 100.0),
                BillWithParticipantCount(2L, "Lunch", System.currentTimeMillis(), 2, 50.0)
            ),
            friends = listOf(Friend(1L, "Alice"), Friend(2L, "Bob")),
            addFriendDialog = false,
            editFriendDialog = false,
            friendName = "",
            editingFriendId = null
        ),
        onEvent = {},
        onBillClick = {}
    )
}

@Preview
@Composable
fun PreviewAddFriendDialog() {
    AddFriendDialog(
        name = "John Doe",
        onNameChange = {},
        onAddClick = {},
        onCancelClick = {},
        onDismissRequest = {},
        isEditMode = false
    )
}

@Preview
@Composable
fun PreviewEditFriendDialog() {
    AddFriendDialog(
        name = "John Doe",
        onNameChange = {},
        onAddClick = {},
        onCancelClick = {},
        onDismissRequest = {},
        isEditMode = true
    )
}

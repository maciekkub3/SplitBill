package com.example.splitbill.ui.Screens.AddBillScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.ui.Screens.AddBillScreen.AddBillIntent.EnterTitle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach

@Composable
fun AddBillScreen(
    viewModel: AddBillViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(AddBillIntent.FetchFriends)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add Bill Screen")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.title,
            onValueChange = { viewModel.handleIntent(EnterTitle(it)) },
            label = { Text("Bill Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        FriendsList(friends = state.friends)

        Button(onClick = { viewModel.handleIntent(AddBillIntent.SaveBill) }) {
            Text(text = "Save Bill")
        }
    }
}

@Composable
fun FriendsList(friends: List<Friend>){
    Column {
        friends.forEach { friend ->
            Text(text = friend.name, modifier = Modifier.padding(8.dp))
        }
    }
}

package com.example.splitbill.ui.screens.billScreen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.splitbill.data.classes.ExpenseItem
import com.example.splitbill.data.local.entity.Friend
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.Expense
import com.example.splitbill.navigation.HomeRoute
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun BillScreenRoot(
    billId: Long,
    navController: NavController,
    viewModel: BillViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(BillIntent.LoadBill(billId))
    }

    BillScreen(
        state = state,
        onEvent = viewModel::handleIntent,
        onNavigateHome = {
            navController.navigate(HomeRoute)
        }
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun BillScreen(
    state: BillUiState,
    onEvent: (BillIntent) -> Unit,
    onNavigateHome: () -> Unit,
    ) {
    if (state.showAddExpenseDialog){
        AddExpenseDialog(
            payer = state.payer,
            options = state.friends,
            description = state.description,
            amount = state.amount,
            onDescriptionChange = { onEvent(BillIntent.DescriptionChange(it)) },
            onPayerChange = { onEvent(BillIntent.PayerChange(it)) },
            onAmountChange = { onEvent(BillIntent.AmountChange(it)) },
            onAddClick = { onEvent(BillIntent.AddExpense(
                description = state.description,
                amount = state.amount.toDoubleOrNull() ?: 0.0,
                paidById = state.payer?.id ?: 0L
            )) },
            onCancelClick = { onEvent(BillIntent.DismissAddExpenseDialog) },
            onDismissRequest = { onEvent(BillIntent.DismissAddExpenseDialog) },
        )
    }

    if (state.showEditExpenseDialog){
        AddExpenseDialog(
            payer = state.payer,
            options = state.friends,
            description = state.description,
            amount = state.amount,
            isEditMode = true,
            onDescriptionChange = { onEvent(BillIntent.DescriptionChange(it)) },
            onPayerChange = { onEvent(BillIntent.PayerChange(it)) },
            onAmountChange = { onEvent(BillIntent.AmountChange(it)) },
            onAddClick = {},
            onCancelClick = { onEvent(BillIntent.DismissEditExpenseDialog) },
            onDismissRequest = { onEvent(BillIntent.DismissEditExpenseDialog) },
        )
    }

    if (state.showSettleUpDialog) {
        SettleUpDialog(
            settlementEntries = state.settlementResult,
            onExitClick = {
                onEvent(BillIntent.DismissSettlementDialog)

            },
            onCloseBillClick = {
                onEvent(BillIntent.DeleteBill)
                onNavigateHome()
            },
            onDismissRequest = { onEvent(BillIntent.DismissSettlementDialog) }
        )
    }

    if (state.showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { onEvent(BillIntent.DismissDeleteDialog) },
            onConfirm = {
                onEvent(BillIntent.DeleteBill)
                onNavigateHome()
            },
            title = "Delete Bill",
            message = "Are you sure you want to delete this bill?",
            icon = Icons.Default.Clear
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Bill",
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Remove Bill",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp)
                        .padding(8.dp)
                        .clickable {
                            onEvent(BillIntent.ShowDeleteDialog)

                        },
                    tint = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${state.bill?.title}", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Total Expense: ${state.totalAmount}")

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            ExpenseList(
                expenses = state.expenses.map { expense ->
                    ExpenseItem(
                        payerName = state.friends.find { it.id == expense.paidById }?.name ?: "Unknown",
                        description = expense.name,
                        date = SimpleDateFormat("dd MMM yyyy").format(Date(expense.date)),
                        amount = expense.amount.toString()
                    )
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onEvent(BillIntent.ShowAddExpenseDialog )},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {

                Text("Add Expense", color = Color.White)
            }

            Button(
                onClick = { onEvent(BillIntent.CalculateSettlement) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("Settle Up" , color = Color.White)
            }
        }
    }
}

@Composable
fun SettleUpDialog(
    settlementEntries: List<SettlementEntry>,
    onExitClick: () -> Unit,
    onCloseBillClick: () -> Unit,
    onDismissRequest: () -> Unit,
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
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Settle Up",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // List of settlements
                if (settlementEntries.isEmpty()) {
                    Text("No settlements needed.", modifier = Modifier.padding(16.dp))
                } else {
                    settlementEntries.forEach { entry ->
                        Text(
                            text = "${entry.from} owes ${entry.to}: ${"%.2f".format(entry.amount)}",
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onExitClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("EXIT", color = Color.White)
                    }

                    Button(
                        onClick = onCloseBillClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Close bill", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseList(expenses: List<ExpenseItem>) {
    LazyColumn {
        items(expenses) { expense ->
            ExpenseItemCard(expense = expense)
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBFFFE3)) // light green
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.payerName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.description
                )
                Text(
                    text = expense.date,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = expense.amount,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AddExpenseDialog(
    options: List<Friend>,
    description: String = "",
    payer: Friend? = null,
    amount: String = "",
    onDescriptionChange: (String) -> Unit,
    onPayerChange: (Friend) -> Unit,
    onAmountChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
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
                WhoPaidDropdown(
                    options = options,
                    selectedOption = payer?.name ?: "Select Payer",
                    onOptionSelected = onPayerChange
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhoPaidDropdown(
    options: List<Friend>,
    selectedOption: String,
    onOptionSelected: (Friend) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Who paid") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { friend ->
                DropdownMenuItem(
                    text = { Text(friend.name) },
                    onClick = {
                        onOptionSelected(friend)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    icon: ImageVector
) {
    Dialog(onDismissRequest = onDismissRequest,) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Button(onClick = onConfirm) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BillScreenPreview() {
    BillScreen(
        state = BillUiState(
            bill = Bill(
                id = 1,
                title = "Dinner with Friends",
            ),
            expenses = listOf(Expense(
                id = 1,
                billId = 1,
                name = "Pizza",
                amount = 50.00,
                date = Date().time,
                paidById = 1
            ), Expense(
                id = 2,
                billId = 1,
                name = "Drinks",
                amount = 75.00,
                date = Date().time,
                paidById = 2
            )),
            totalAmount = 125.00,
            friends = listOf(Friend(1, "Alice"), Friend(2, "Bob")),
            payer = null,
            description = "",
            amount = "",
            showAddExpenseDialog = false,
            showEditExpenseDialog = false,
            showSettleUpDialog = false,
            showDeleteDialog = false,
            settlementResult = emptyList()
        ),
        onEvent = {},
        onNavigateHome = {}
    )
}

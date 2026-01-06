package com.application.instagramcloneapp.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.instagramcloneapp.screen.singup.PrimaryColor

@Preview
@Composable
fun ButtonWidget(
    modifier: Modifier = Modifier,
    click:()->Unit = {}
){
    Button(onClick = {click.invoke()},
        shape = RectangleShape,
        modifier = modifier.padding(2.dp)
    ) {

    }
}


@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    phoneField: Boolean = false,
    passwordField: Boolean = false,
    isVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    icon: ImageVector,
    keyboardType: KeyboardType,
    isLastItem: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) },
        trailingIcon = { if (passwordField){
            val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onVisibilityChange) {
                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
            }
        }
        },
        visualTransformation = if (passwordField && !isVisible) PasswordVisualTransformation()  else VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = PrimaryColor
        ),
        prefix = { if (phoneField) Text(text = "+91 ", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (isLastItem) ImeAction.Done else ImeAction.Next

        ),
        singleLine = true
    )
}

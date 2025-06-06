package fpl.md19.beefashion.models

data class Products (
    val id : String,
    val name : String,
    val price : Int,
    val categoryId : String,
    val image : String,
    val isFavByCurrentUser: Boolean,
    val quantities : List<Int> = emptyList(),
    val createdAt : String
)
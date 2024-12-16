import kotlin.math.max

fun main() {
    val guitar = Good(
        name = "guitar",
        weight = 1,
        price = 1500
    )
    val recorder = Good(
        name = "recorder",
        weight = 4,
        price = 3000,
    )
    val notebook = Good(
        name = "notebook",
        weight = 3,
        price = 2000,
    )
    val iphone = Good(
        name = "iphone",
        weight = 1,
        price = 2000,
    )

    val brilliant = Good(
        name = "brilliant",
        weight = 3,
        price = 1000000,
    )

    val tableConfig = mapTableConfig(
        goods = listOf(iphone, recorder, notebook, guitar, brilliant),
        maxCapacity = 4,
    )
    val maximumBenefitResult = getMaximumBenefit(tableConfig)
    println("Максимальная ценность, которую можно унести: ${maximumBenefitResult.price}")
    println("Список товаров, которые можно унести: ${maximumBenefitResult.goods.joinToString(", ") { it.name }}")
}

fun getMaximumBenefit(tableConfig: TableConfig): MaximumBenefitResult {
    // Создаём таблицу для хранения максимальных ценностей
    val weights = tableConfig.weights
    val prices = tableConfig.prices
    val maxCapacity = tableConfig.maxCapacity

    // Массивы больше на единицу, чтобы можно было обращаться к предыдущим индексам без ошибок
    val table = Array(weights.size + 1) { IntArray(maxCapacity + 1) }
    // Заполняем таблицу
    for (weightIndex in 1..weights.size) {
        for (capacity in 0..maxCapacity) {
            val previousWeightIndex = weightIndex - 1
            // Если вес предмета больше вместимости, то копируем данные из предыдущей строки
            // и идем дальше
            if (capacity < weights[previousWeightIndex]) {
                table[weightIndex][capacity] = table[previousWeightIndex][capacity]
            } else {
                val currentPrice = prices[previousWeightIndex]
                // Выбираем максимум между предыщущей стоимостью в таблице и
                // максимальной стоимостью оставшегося пространства + текущая стоимость
                table[weightIndex][capacity] = max(
                    a = table[previousWeightIndex][capacity],
                    b = table[previousWeightIndex][capacity - weights[previousWeightIndex]] + currentPrice,
                )
            }
        }
    }

    printTable(table)

    // Возвращаем максимальную ценность
    val maxBenefitPrice = table[weights.size][maxCapacity]
    val maxBenefitGoods = getMaxBenefitGoods(tableConfig, table)
    return MaximumBenefitResult(
        price = maxBenefitPrice,
        goods = maxBenefitGoods
    )
}

private fun getMaxBenefitGoods(tableConfig: TableConfig, table: Array<IntArray>): List<Good> {
    val maxCapacity = tableConfig.maxCapacity
    val weights = tableConfig.weights
    // Отслеживаем список выбранных предметов
    val selectedItemIndexes = mutableListOf<Int>()
    var capacity = maxCapacity
    // Двигаемся назад по таблице
    for (weightIndex in weights.size downTo 1) {
        println("weightIndex: ${weightIndex - 1}")
        println("capacity: $capacity")
        println("table[weightIndex][capacity]: ${table[weightIndex][capacity]}")
        println("table[weightIndex - 1][capacity]: ${table[weightIndex - 1][capacity]}")

        // Если текущая ценность не равна предыдущей, значит, текущий предмет был выбран
        if (table[weightIndex][capacity] != table[weightIndex - 1][capacity]) {
            // - 1 так как есть сдвиг массива
            selectedItemIndexes.add(weightIndex - 1)
            // Уменьшаем вес на вес текущего предмета
            capacity -= weights[weightIndex - 1]
        }
        println()
    }
    return selectedItemIndexes.map { index -> tableConfig.goods[index] }.reversed()
}

private fun mapTableConfig(goods: List<Good>, maxCapacity: Int): TableConfig {
    val weights = IntArray(goods.size)
    val prices = IntArray(goods.size)
    goods.forEachIndexed { index, good ->
        weights[index] = good.weight
        prices[index] = good.price
    }
    return TableConfig(
        weights = weights,
        prices = prices,
        maxCapacity = maxCapacity,
        goods = goods,
    )
}

private fun printTable(table: Array<IntArray>) {
    if (table.isEmpty()) {
        return
    }
    for (element in table) {
        for (col in 0..<table[0].size) {
            print("${element[col]} ")
        }
        println()
    }
}

class TableConfig(
    val weights: IntArray,
    val prices: IntArray,
    val maxCapacity: Int,
    val goods: List<Good>,
)

data class Good(
    val name: String,
    val weight: Int,
    val price: Int,
)

data class MaximumBenefitResult(
    val price: Int,
    val goods: List<Good>,
)


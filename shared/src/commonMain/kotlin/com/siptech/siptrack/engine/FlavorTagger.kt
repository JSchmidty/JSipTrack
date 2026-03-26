package com.siptech.siptrack.engine

/**
 * FlavorTagger — Maps beverage descriptors to structured flavor wheel tags.
 *
 * Used for: professional tasting notes, product enrichment, search filtering.
 * Primary categories follow the Beer/Wine/Spirits flavor wheel conventions.
 */
object FlavorTagger {

    enum class FlavorCategory(val displayName: String) {
        FRUITY("Fruity"),
        FLORAL("Floral"),
        HERBAL("Herbal"),
        SPICY("Spicy"),
        EARTHY("Earthy"),
        NUTTY("Nutty"),
        SWEET("Sweet"),
        SOUR("Sour"),
        BITTER("Bitter"),
        SALTY("Salty"),
        UMAMI("Umami"),
        SMOKY("Smoky"),
        WOODY("Woody"),
        ROASTY("Roasty"),
        FUNKY("Funky"),
        MINERALLY("Minerally"),
    }

    private val keywordMap: Map<String, FlavorCategory> = mapOf(
        // Fruity
        "apple" to FlavorCategory.FRUITY,
        "pear" to FlavorCategory.FRUITY,
        "citrus" to FlavorCategory.FRUITY,
        "lemon" to FlavorCategory.FRUITY,
        "lime" to FlavorCategory.FRUITY,
        "orange" to FlavorCategory.FRUITY,
        "grapefruit" to FlavorCategory.FRUITY,
        "berry" to FlavorCategory.FRUITY,
        "strawberry" to FlavorCategory.FRUITY,
        "raspberry" to FlavorCategory.FRUITY,
        "blueberry" to FlavorCategory.FRUITY,
        "blackberry" to FlavorCategory.FRUITY,
        "cherry" to FlavorCategory.FRUITY,
        "plum" to FlavorCategory.FRUITY,
        "peach" to FlavorCategory.FRUITY,
        "apricot" to FlavorCategory.FRUITY,
        "mango" to FlavorCategory.FRUITY,
        "pineapple" to FlavorCategory.FRUITY,
        "banana" to FlavorCategory.FRUITY,
        "tropical" to FlavorCategory.FRUITY,
        "melon" to FlavorCategory.FRUITY,
        "fig" to FlavorCategory.FRUITY,
        "grape" to FlavorCategory.FRUITY,
        "raisin" to FlavorCategory.FRUITY,
        "currant" to FlavorCategory.FRUITY,
        // Floral
        "floral" to FlavorCategory.FLORAL,
        "rose" to FlavorCategory.FLORAL,
        "violet" to FlavorCategory.FLORAL,
        "lavender" to FlavorCategory.FLORAL,
        "jasmine" to FlavorCategory.FLORAL,
        "elderflower" to FlavorCategory.FLORAL,
        "honeysuckle" to FlavorCategory.FLORAL,
        "hibiscus" to FlavorCategory.FLORAL,
        // Herbal
        "herbal" to FlavorCategory.HERBAL,
        "grassy" to FlavorCategory.HERBAL,
        "mint" to FlavorCategory.HERBAL,
        "menthol" to FlavorCategory.HERBAL,
        "eucalyptus" to FlavorCategory.HERBAL,
        "sage" to FlavorCategory.HERBAL,
        "thyme" to FlavorCategory.HERBAL,
        "basil" to FlavorCategory.HERBAL,
        "pine" to FlavorCategory.HERBAL,
        "hops" to FlavorCategory.HERBAL,
        "resinous" to FlavorCategory.HERBAL,
        "vegetal" to FlavorCategory.HERBAL,
        "hay" to FlavorCategory.HERBAL,
        // Spicy
        "spice" to FlavorCategory.SPICY,
        "spicy" to FlavorCategory.SPICY,
        "pepper" to FlavorCategory.SPICY,
        "cinnamon" to FlavorCategory.SPICY,
        "clove" to FlavorCategory.SPICY,
        "nutmeg" to FlavorCategory.SPICY,
        "ginger" to FlavorCategory.SPICY,
        "anise" to FlavorCategory.SPICY,
        "licorice" to FlavorCategory.SPICY,
        "cardamom" to FlavorCategory.SPICY,
        "allspice" to FlavorCategory.SPICY,
        // Earthy
        "earthy" to FlavorCategory.EARTHY,
        "mushroom" to FlavorCategory.EARTHY,
        "forest floor" to FlavorCategory.EARTHY,
        "damp" to FlavorCategory.EARTHY,
        "wet stone" to FlavorCategory.EARTHY,
        "terroir" to FlavorCategory.EARTHY,
        "dirt" to FlavorCategory.EARTHY,
        "truffle" to FlavorCategory.EARTHY,
        // Nutty
        "nutty" to FlavorCategory.NUTTY,
        "almond" to FlavorCategory.NUTTY,
        "walnut" to FlavorCategory.NUTTY,
        "hazelnut" to FlavorCategory.NUTTY,
        "pecan" to FlavorCategory.NUTTY,
        "cashew" to FlavorCategory.NUTTY,
        "marzipan" to FlavorCategory.NUTTY,
        // Sweet
        "sweet" to FlavorCategory.SWEET,
        "honey" to FlavorCategory.SWEET,
        "caramel" to FlavorCategory.SWEET,
        "toffee" to FlavorCategory.SWEET,
        "butterscotch" to FlavorCategory.SWEET,
        "vanilla" to FlavorCategory.SWEET,
        "chocolate" to FlavorCategory.SWEET,
        "cocoa" to FlavorCategory.SWEET,
        "molasses" to FlavorCategory.SWEET,
        "maple" to FlavorCategory.SWEET,
        "sugar" to FlavorCategory.SWEET,
        "candy" to FlavorCategory.SWEET,
        // Sour / Tart
        "sour" to FlavorCategory.SOUR,
        "tart" to FlavorCategory.SOUR,
        "acidic" to FlavorCategory.SOUR,
        "vinegar" to FlavorCategory.SOUR,
        "lactic" to FlavorCategory.SOUR,
        "bright" to FlavorCategory.SOUR,
        "crisp" to FlavorCategory.SOUR,
        // Bitter
        "bitter" to FlavorCategory.BITTER,
        "tannic" to FlavorCategory.BITTER,
        "astringent" to FlavorCategory.BITTER,
        "dry" to FlavorCategory.BITTER,
        "coffee" to FlavorCategory.BITTER,
        "espresso" to FlavorCategory.BITTER,
        "dark chocolate" to FlavorCategory.BITTER,
        "grapefruit pith" to FlavorCategory.BITTER,
        // Smoky
        "smoky" to FlavorCategory.SMOKY,
        "smoke" to FlavorCategory.SMOKY,
        "peaty" to FlavorCategory.SMOKY,
        "peat" to FlavorCategory.SMOKY,
        "campfire" to FlavorCategory.SMOKY,
        "charcoal" to FlavorCategory.SMOKY,
        "ash" to FlavorCategory.SMOKY,
        "barbecue" to FlavorCategory.SMOKY,
        "mezcal" to FlavorCategory.SMOKY,
        // Woody / Oak
        "oak" to FlavorCategory.WOODY,
        "woody" to FlavorCategory.WOODY,
        "cedar" to FlavorCategory.WOODY,
        "sandalwood" to FlavorCategory.WOODY,
        "barrel" to FlavorCategory.WOODY,
        "toasted oak" to FlavorCategory.WOODY,
        "sawdust" to FlavorCategory.WOODY,
        // Roasty
        "roasty" to FlavorCategory.ROASTY,
        "roasted" to FlavorCategory.ROASTY,
        "malt" to FlavorCategory.ROASTY,
        "toast" to FlavorCategory.ROASTY,
        "biscuit" to FlavorCategory.ROASTY,
        "bread" to FlavorCategory.ROASTY,
        "grain" to FlavorCategory.ROASTY,
        "cereal" to FlavorCategory.ROASTY,
        // Funky
        "funky" to FlavorCategory.FUNKY,
        "barnyard" to FlavorCategory.FUNKY,
        "brett" to FlavorCategory.FUNKY,
        "farmhouse" to FlavorCategory.FUNKY,
        "leather" to FlavorCategory.FUNKY,
        "game" to FlavorCategory.FUNKY,
        "cheese" to FlavorCategory.FUNKY,
        "yeasty" to FlavorCategory.FUNKY,
        "sulfur" to FlavorCategory.FUNKY,
        // Minerally
        "mineral" to FlavorCategory.MINERALLY,
        "minerally" to FlavorCategory.MINERALLY,
        "flint" to FlavorCategory.MINERALLY,
        "slate" to FlavorCategory.MINERALLY,
        "chalk" to FlavorCategory.MINERALLY,
        "saline" to FlavorCategory.MINERALLY,
        "oyster shell" to FlavorCategory.MINERALLY,
        "limestone" to FlavorCategory.MINERALLY,
    )

    /**
     * Tag a free-text tasting note with matching FlavorCategory values.
     * Returns a deduplicated list of matched categories, ordered by first match.
     */
    fun tag(tastingNote: String): List<FlavorCategory> {
        val lower = tastingNote.lowercase()
        val matched = mutableListOf<FlavorCategory>()
        for ((keyword, category) in keywordMap) {
            if (lower.contains(keyword) && category !in matched) {
                matched.add(category)
            }
        }
        return matched
    }

    /**
     * Tag a list of descriptor strings (e.g. from a product's flavorProfile field).
     */
    fun tagDescriptors(descriptors: List<String>): List<FlavorCategory> =
        tag(descriptors.joinToString(", "))

    /**
     * Returns display names for the tags (for UI rendering).
     */
    fun tagNames(tastingNote: String): List<String> =
        tag(tastingNote).map { it.displayName }
}

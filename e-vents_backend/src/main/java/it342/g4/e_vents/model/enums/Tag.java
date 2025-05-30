package it342.g4.e_vents.model.enums;

public enum Tag {
    // Music tags
    POP(Category.MUSIC),
    HIP_HOP_RAP(Category.MUSIC),
    RNB(Category.MUSIC),
    ROCK(Category.MUSIC),
    ELECTRONIC(Category.MUSIC),
    COUNTRY(Category.MUSIC),

    // Sports tags
    BASKETBALL(Category.SPORTS),
    FOOTBALL(Category.SPORTS),
    VOLLEYBALL(Category.SPORTS),
    TENNIS(Category.SPORTS),
    BASEBALL(Category.SPORTS),

    // Theatre tags
    DRAMA(Category.THEATRE),
    MUSICAL(Category.THEATRE),
    OPERA(Category.THEATRE),

    // Comedy tags
    STAND_UP(Category.COMEDY),
    SKETCH(Category.COMEDY),
    IMPROV(Category.COMEDY),

    // Education tags
    WORKSHOP(Category.EDUCATION),
    SEMINAR(Category.EDUCATION),
    CONFERENCE(Category.EDUCATION),
    WEBINAR(Category.EDUCATION),

    // Other tags
    COMMUNITY(Category.OTHER),
    FUNDRAISER(Category.OTHER),
    CULTURAL(Category.OTHER),
    NETWORKING(Category.OTHER);

    private final Category category;

    Tag(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public static Tag[] getTagsByCategory(Category category) {
        return java.util.Arrays.stream(Tag.values())
            .filter(tag -> tag.getCategory() == category)
            .toArray(Tag[]::new);
    }
}
package me.trysam.extremewands.util;

import org.bukkit.NamespacedKey;

public class Namespaces {


    private final Namespaces.Wand wand;
    private final Namespaces.Book book;
    private final Namespaces.Spell spell;
    private final Namespaces.Special special;
    private final Namespaces.Recipes recipes;

    public Namespaces(NamespacedKey wandType, NamespacedKey isWandActivated, NamespacedKey wandOwner,
                      NamespacedKey wandEffectMultiplier, NamespacedKey wandProtectionMultiplier, NamespacedKey wandUuid,
                      NamespacedKey isAncientKnowledgeBook, NamespacedKey selectedSpell, NamespacedKey selectedUpgrade,
                      NamespacedKey canSelectUpgrade, NamespacedKey pageSelector, NamespacedKey firingModeSelector,
                      NamespacedKey woodenWand, NamespacedKey boneWand, NamespacedKey blazeWand, NamespacedKey enderWand) {
        this.wand = new Wand(wandType, isWandActivated, wandOwner, wandEffectMultiplier, wandProtectionMultiplier, wandUuid);
        this.book = new Book(isAncientKnowledgeBook);
        this.spell = new Spell(selectedSpell, selectedUpgrade, canSelectUpgrade);
        this.special = new Special(pageSelector, firingModeSelector);
        this.recipes = new Recipes(woodenWand, boneWand, blazeWand, enderWand);
    }

    public Wand getWand() {
        return wand;
    }

    public Book getBook() {
        return book;
    }

    public Spell getSpell() {
        return spell;
    }

    public Special getSpecial() {
        return special;
    }

    public Recipes getRecipes() {
        return recipes;
    }

    public class Wand {
        private NamespacedKey type;
        private NamespacedKey isActivated;
        private NamespacedKey owner;
        private NamespacedKey effectMultiplier;
        private NamespacedKey protectionMultiplier;
        private NamespacedKey wandUuid;

        public Wand(NamespacedKey type, NamespacedKey isActivated, NamespacedKey owner, NamespacedKey effectMultiplier, NamespacedKey protectionMultiplier, NamespacedKey wandUuid) {
            this.type = type;
            this.isActivated = isActivated;
            this.owner = owner;
            this.effectMultiplier = effectMultiplier;
            this.protectionMultiplier = protectionMultiplier;
            this.wandUuid = wandUuid;
        }

        public NamespacedKey getType() {
            return type;
        }

        public NamespacedKey getIsActivated() {
            return isActivated;
        }

        public NamespacedKey getOwner() {
            return owner;
        }

        public NamespacedKey getEffectMultiplier() {
            return effectMultiplier;
        }

        public NamespacedKey getProtectionMultiplier() {
            return protectionMultiplier;
        }

        public NamespacedKey getWandUuid() {
            return wandUuid;
        }
    }

    public class Book {
        private NamespacedKey isAncientKnowledgeBook;

        public Book(NamespacedKey isAncientKnowledgeBook) {
            this.isAncientKnowledgeBook = isAncientKnowledgeBook;
        }

        public NamespacedKey getIsAncientKnowledgeBook() {
            return isAncientKnowledgeBook;
        }
    }

    public class Spell {
        private NamespacedKey selectedSpell;
        private NamespacedKey selectedUpgrade;
        private NamespacedKey canSelectUpgrade;

        public Spell(NamespacedKey selectedSpell, NamespacedKey selectedUpgrade, NamespacedKey canSelectUpgrade) {
            this.selectedSpell = selectedSpell;
            this.selectedUpgrade = selectedUpgrade;
            this.canSelectUpgrade = canSelectUpgrade;
        }

        public NamespacedKey getSelectedSpell() {
            return selectedSpell;
        }

        public NamespacedKey getSelectedUpgrade() {
            return selectedUpgrade;
        }

        public NamespacedKey getCanSelectUpgrade() {
            return canSelectUpgrade;
        }
    }

    public class Special {
        private NamespacedKey pageSelector;
        private NamespacedKey firingModeSelector;

        public Special(NamespacedKey pageSelector, NamespacedKey firingModeSelector) {
            this.pageSelector = pageSelector;
            this.firingModeSelector = firingModeSelector;
        }

        public NamespacedKey getPageSelector() {
            return pageSelector;
        }

        public NamespacedKey getFiringModeSelector() {
            return firingModeSelector;
        }
    }

    public class Recipes {
        private NamespacedKey woodenWand;
        private NamespacedKey boneWand;
        private NamespacedKey blazeWand;
        private NamespacedKey enderWand;

        public Recipes(NamespacedKey woodenWand, NamespacedKey boneWand, NamespacedKey blazeWand, NamespacedKey enderWand) {
            this.woodenWand = woodenWand;
            this.boneWand = boneWand;
            this.blazeWand = blazeWand;
            this.enderWand = enderWand;
        }

        public NamespacedKey getWoodenWand() {
            return woodenWand;
        }

        public NamespacedKey getBoneWand() {
            return boneWand;
        }

        public NamespacedKey getBlazeWand() {
            return blazeWand;
        }

        public NamespacedKey getEnderWand() {
            return enderWand;
        }
    }



}

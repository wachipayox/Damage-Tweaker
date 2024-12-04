**Welcome to the Damage Tweaker GitHub!**
The mod has been released just when im writing this text. Any purpose to adding new features or enhancing existing ones
are welcomed!

-----
# GUIDE FOR DEVS USE:

### Installing the API in your mod.

Add this repository to the gradle: (yes its my username and token, the token is for only package read)

`
maven {
url "https://maven.pkg.github.com/wachipayox/*"
credentials {
            username = "wachipayox"
            password = "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"
        }
}
`

For adding the mod the package is:
"com.wachi.damagetweaker:damage-tweaker:VERSION"

---
The maven info for packages can be found in:
https://github.com/wachipayox?tab=packages&repo_name=damage-tweaker


I recommend only adding the mod to the compile and not the runtime (when publishing),
for not creating conflicts with other mods that uses this one._

---
### Modifying values

Now that you have the mod in your gradle, the class you are interested in is in DamageConfig.
There is a Map called def_map, that is the default values map, when the user doesn't have a specific config for a damage
cooldown property, the game tries to get a value from this map before obtaining the general/default value (priority explained in
the following section). Enter to the DamageConfig class and see the def_map declaration for understanding the map syntax.
If you put an entity/damage that doesn't exist there will be no problems!

----------

#### **References:** 
Also, you can use "any mob" for making a reference to any source entity in the game or "any damage" to make a reference to
any damage source in the game.

The values also can use "default" instead of a number, that makes the game take the real default value.

The default values for properties (which are located in the "damage_tweaker-server.toml" file) can be overlapped using the Config class, that
has variables like def_cooldown... but I recommend not touching them. Its more reliable putting in the def_map an "any mob" and "any_damage" entry.

-----

For modifying the map you can do it whenever you want, it's a static declaration so you can do it at the server start or even
your mod common setup.

----------

#### **Priority:**
The game chose the value in this order(Higher prior to lower prior):
1. Specific entity and specific damage from config file.
2. Specific entity and specific damage from def_map.
3. Specific entity and any damage from config file.
4. Specific entity and any damage from def_map.
5. Any entity and specific damage from config file.
6. Any entity and specific damage from def_map.
7. Any entity and any damage from config file. (this usually doesn't exist)
8. Any entity and any damage from def_map.
9. Default value

The game uses a statement if is the last (Default value) or if isn't empty and isn't "default". Else, checks the next one.

-----

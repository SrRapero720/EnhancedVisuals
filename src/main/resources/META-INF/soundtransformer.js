function initializeCoreMod() {
	print("Init EnhancedVisuals coremods ...")
    return {
        'soundtransformer': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.audio.SoundEngine',
				'methodName': 'func_188770_e',
				'methodDesc': '(Lnet/minecraft/client/audio/ISound;)F'
            },
            'transformer': function(method) {
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var next = method.instructions.getFirst();
				
				while(next.getNext() !== null) {
					if(next.getOpcode() == Opcodes.FRETURN) {
						asmapi.log("INFO", "Found return");
						method.instructions.insertBefore(next, asmapi.buildMethodCall("team/creative/enhancedvisuals/client/sound/SoundMuteHandler", "getClampedVolume", "(F)F", asmapi.MethodType.STATIC));
						return method;
					}
					next = next.getNext();
				}
                return method;
            }
		}
    }
}

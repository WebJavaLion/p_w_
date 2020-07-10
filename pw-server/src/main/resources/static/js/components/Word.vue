<template>
    <v-row dense>
        <v-col cols="12" sm="12" md="6">
            <v-card class="pa-2 ml-2" color="grey lighten-2">
                <v-row dense class="ma-0" justify="start">
                    <v-col cols="12" sm="12" md="4">
                        <v-text-field placeholder="original"
                                      label="original"
                                      v-if="isRedacted"
                                      v-model="redacted.original">
                        </v-text-field>
                        <span v-else>
                            <v-card-subtitle v-if="vuetifyBreakpoint !== 'sm' && vuetifyBreakpoint !== 'xs'"
                                             class="pa-0 text-md-center"
                                             style="font-size: 11px; height: 15px">
                                original
                            </v-card-subtitle>
                            <v-card-text class="font-weight-medium text-center pb-0 pt-0">
                                {{wordOriginal}}
                            </v-card-text>
                        </span>
                    </v-col>
                    <v-col cols="12" md="1" sm="12">
                        <v-card-actions class="pt-md-3 pa-0 justify-center">
                            <v-icon dense color="green">{{translateIcon}}</v-icon>
                        </v-card-actions>
                    </v-col>
                    <v-col cols="12" sm="12" md="4">
                        <v-text-field placeholder="translation"
                                      label="translation"
                                      v-if="isRedacted"
                                      v-model="redacted.translated">
                        </v-text-field>
                        <span v-else>
                            <v-card-subtitle v-if="vuetifyBreakpoint !== 'sm' && vuetifyBreakpoint !== 'xs'"
                                             class="pa-0 text-md-center"
                                             style="font-size: 11px; height: 15px">
                                translation
                            </v-card-subtitle>
                            <v-card-text class="font-weight-medium text-center pb-0 pt-0">
                                {{wordTranslation}}
                            </v-card-text>
                        </span>
                    </v-col>
                    <v-col cols="4" md="2" sm="3" lg="2">
                        <v-card-actions class="justify-md-center pt-0 pt-md-2 pb-sm-0">
                            <v-btn v-if="!isRedacted" small v-on:click="isRedacted = !isRedacted">
                                <v-icon left dense color="green">{{editIcon}}</v-icon>
                                edit
                            </v-btn>
                            <v-btn v-if="isRedacted"
                                   icon small
                                   v-on:click="updateAndSetDefault({
                                        id: id,
                                        wordOriginal: redacted.original,
                                        wordTranslation: redacted.translated
                                   })">
                                <v-icon dense color="green">{{saveIcon}}</v-icon>
                            </v-btn>
                            <v-btn v-if="isRedacted" icon small v-on:click="clearFields">
                                <v-icon dense color="red">{{cancelIcon}}</v-icon>
                            </v-btn>
                            <v-btn small icon v-on:click="deleteWordByIdAction(id)">
                                <v-icon dense color="red">{{deleteIcon}}</v-icon>
                            </v-btn>
                        </v-card-actions>
                    </v-col>
                </v-row>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
    import {mapActions} from "vuex";
    import {mdiCancel} from '@mdi/js'
    import {mdiDelete} from '@mdi/js'
    import {mdiImageEdit} from '@mdi/js'
    import {mdiContentSave} from '@mdi/js'
    import {mdiCompareHorizontal} from '@mdi/js'
    export default {
        data() {
            return {
                isRedacted: false,
                cancelIcon: mdiCancel,
                deleteIcon: mdiDelete,
                editIcon: mdiImageEdit,
                saveIcon: mdiContentSave,
                translateIcon: mdiCompareHorizontal,
                redacted: {
                    original: this.wordOriginal,
                    translated: this.wordTranslation
                }
            }
        },
        props: ['id', 'wordOriginal', 'wordTranslation'],
        methods: {
            ...mapActions(['deleteWordByIdAction', 'updateWordAction']),
            updateAndSetDefault(word) {
                let call = () => this.isRedacted = false;
                this.updateWordAction({
                    word: word,
                    callback: () => call()
                })
            },
            clearFields() {
                this.redacted.original = this.wordOriginal;
                this.redacted.translated = this.wordTranslation;
                this.isRedacted = false;
            }
        },
        computed: {
            vuetifyBreakpoint() {
                return this.$vuetify.breakpoint.name;
            }
        }
    }
</script>

<style>

</style>
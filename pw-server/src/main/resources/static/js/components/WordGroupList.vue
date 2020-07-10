<template>
    <v-container class="pa-0 pa-md-2">
        <v-row justify="start" dense>
            <Group v-for="group in getWordGroups"
                   :key="group.id"
                   :groupId="group.id">
            </Group>
        </v-row>
    </v-container>
</template>


<script>
    import {mapGetters} from "vuex";
    import Group from "./Group.vue";

    export default {
        components: {Group},
        computed: mapGetters(['getWordGroups'])
    }
</script>


<style>


</style>